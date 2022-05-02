package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.*;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import cn.fantasticmao.grpckit.support.ProtoUtil;
import io.grpc.Channel;
import io.grpc.ServiceDescriptor;
import io.grpc.stub.AbstractStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
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
public class GrpcStubBeanPostProcessor implements BeanPostProcessor, Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcStubBeanPostProcessor.class);

    /**
     * Cache of gRPC service names, keyed by the gRPC stub class.
     */
    private final ConcurrentHashMap<Class<?>, String> serviceNameCache = new ConcurrentHashMap<>(128);

    /**
     * Cache of gRPC {@link io.grpc.Channel Channel}s, keyed by the application name.
     */
    private final ConcurrentHashMap<String, Channel> channelCache = new ConcurrentHashMap<>(64);

    private final GrpcKitConfig grpcKitConfig;

    public GrpcStubBeanPostProcessor(@Nonnull GrpcKitConfig grpcKitConfig) {
        this.grpcKitConfig = grpcKitConfig;
    }

    public String getServiceName(Class<?> stubClass) {
        return serviceNameCache.computeIfAbsent(stubClass, key -> {
                ServiceDescriptor serviceDescriptor = ProtoUtil.getServiceDescriptor(stubClass);
                return serviceDescriptor.getName();
            }
        );
    }

    public String getAppName(String serviceName) {
        ApplicationMetadata applicationMetadata = ApplicationMetadataCache.getInstance()
            .getByServiceName(serviceName);
        return applicationMetadata.getName();
    }

    public Channel getChannel(String appName) {
        // FIXME
        return channelCache.computeIfAbsent(appName, key ->
            GrpcKitChannelBuilder.forConfig(appName, this.grpcKitConfig)
                .usePlaintext()
                .build()
        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
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

    private class GrpcClientFieldElement extends InjectionMetadata.InjectedElement {
        private final String tag;
        private final int timeout;

        public GrpcClientFieldElement(Field field, String tag, int timeout) {
            super(field, null);
            this.tag = tag;
            this.timeout = timeout;
        }

        @Override
        protected Object getResourceToInject(@Nonnull Object target, @Nullable String requestingBeanName) {
            Class<?> resourceType = super.getResourceType();
            @SuppressWarnings("unchecked")
            Class<? extends AbstractStub> stubClass = (Class<? extends AbstractStub>) resourceType;
            // stub class -> service name
            String serviceName = GrpcStubBeanPostProcessor.this.getServiceName(stubClass);
            // service name -> application name
            String appName = GrpcStubBeanPostProcessor.this.getAppName(serviceName);
            // application name -> channel
            Channel channel = GrpcStubBeanPostProcessor.this.getChannel(appName);
            // FIXME
            return GrpcKitStubFactory.newStub(stubClass, channel, tag, timeout);
        }
    }

}
