package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.*;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import cn.fantasticmao.grpckit.springboot.factory.GrpcKitChannelBuilderFactory;
import cn.fantasticmao.grpckit.support.ProtoUtil;
import io.grpc.Channel;
import io.grpc.ServiceDescriptor;
import io.grpc.stub.AbstractStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link BeanPostProcessor Spring BeanPostProcessor} implementation that
 * supports the {@link GrpcClient GrpcClient} annotation.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp">Customizing Beans by Using a BeanPostProcessor</a>
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 * @see org.springframework.context.annotation.CommonAnnotationBeanPostProcessor
 * @since 2022-04-03
 */
public class GrpcStubBeanPostProcessor implements Ordered, BeanFactoryAware, BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcStubBeanPostProcessor.class);

    /**
     * Cache of gRPC service names, keyed by the gRPC stub class.
     */
    private final ConcurrentHashMap<Class<?>, String> serviceNameCache = new ConcurrentHashMap<>(128);

    /**
     * Cache of gRPC {@link io.grpc.Channel Channel}s, keyed by the application name.
     */
    private final ConcurrentHashMap<String, Channel> channelCache = new ConcurrentHashMap<>(64);

    @Nullable
    private GrpcKitConfig grpcKitConfig;
    @Nullable
    private GrpcKitChannelBuilderFactory grpcKitChannelBuilderFactory;

    public GrpcStubBeanPostProcessor() {
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.grpcKitConfig = beanFactory.getBean(
            GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_CONFIG, GrpcKitConfig.class);

        try {
            this.grpcKitChannelBuilderFactory = beanFactory.getBean(GrpcKitChannelBuilderFactory.class);
        } catch (NoSuchBeanDefinitionException e) {
            this.grpcKitChannelBuilderFactory = GrpcKitChannelBuilderFactory.Default.INSTANCE;
        }
    }

    @Override
    public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        this.processInjection(bean, beanName);
        return bean;
    }

    private void processInjection(Object bean, String beanName) throws BeanCreationException {
        final Class<?> clazz = bean.getClass();
        InjectionMetadata metadata = this.buildGrpcClientInjectedMetadata(clazz);
        try {
            metadata.inject(bean, beanName, null);
        } catch (BeanCreationException e) {
            throw e;
        } catch (Throwable e) {
            throw new BeanCreationException(
                "Injection of @GrpcClient dependencies failed for class [" + clazz + "]", e);
        }
    }

    private InjectionMetadata buildGrpcClientInjectedMetadata(Class<?> clazz) {
        final List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotations annotations = MergedAnnotations.from(field);
                MergedAnnotation<GrpcClient> annotation = annotations.get(GrpcClient.class);
                if (!annotation.isPresent()) {
                    return;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new GrpcKitException(
                        "@GrpcClient annotation is not supported on the static field: " + field);
                }
                if (!AbstractStub.class.isAssignableFrom(field.getType())) {
                    throw new GrpcKitException(
                        "@GrpcClient annotation is not supported on the class: " + field.getType());
                }

                final String tag = annotation.getString("tag");
                final int timeout = annotation.getInt("timeout");
                currElements.add(new GrpcClientFieldElement(field, tag, timeout));
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);
    }

    private <S extends AbstractStub<S>> String getServiceName(Class<S> stubClass) {
        return serviceNameCache.computeIfAbsent(stubClass, key -> {
            ServiceDescriptor serviceDescriptor = ProtoUtil.getServiceDescriptor(stubClass);
            return serviceDescriptor.getName();
        });
    }

    private String getDependApplicationName(String serviceName) {
        ApplicationMetadata applicationMetadata = ApplicationMetadataCache.getInstance()
            .getByServiceName(serviceName);
        return applicationMetadata.getName();
    }

    private Channel getChannel(String appName) {
        Objects.requireNonNull(this.grpcKitConfig, "grpcKitConfig must not be null");
        Objects.requireNonNull(this.grpcKitChannelBuilderFactory, "grpcKitChannelBuilderFactory must not be null");
        return channelCache.computeIfAbsent(appName, key -> {
            GrpcKitChannelBuilder builder = GrpcKitChannelBuilder.forConfig(appName, this.grpcKitConfig);
            builder = grpcKitChannelBuilderFactory.maintain(builder);
            return builder.build();
        });
    }

    private class GrpcClientFieldElement extends InjectionMetadata.InjectedElement {
        private final String tag;
        private final int timeout;

        public GrpcClientFieldElement(Field field, String tag, int timeout) {
            super(field, null);
            this.tag = tag;
            this.timeout = timeout;
        }

        @Override
        protected void inject(@Nonnull Object bean, @Nullable String requestingBeanName, @Nullable PropertyValues pvs)
            throws Throwable {
            Field field = (Field) super.member;
            Object stub = this.newStubObject(field, bean);
            if (stub != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, stub);
            }
        }

        protected <S extends AbstractStub<S>> S newStubObject(Field field, Object bean) {
            @SuppressWarnings("unchecked")
            Class<S> stubClass = (Class<S>) field.getType();
            // stub class -> service name
            String serviceName = GrpcStubBeanPostProcessor.this.getServiceName(stubClass);
            // service name -> application name
            String appName = GrpcStubBeanPostProcessor.this.getDependApplicationName(serviceName);
            // application name -> channel
            Channel channel = GrpcStubBeanPostProcessor.this.getChannel(appName);
            return GrpcKitStubFactory.newStub(stubClass, channel, tag, timeout);
        }
    }

}
