package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import io.grpc.stub.AbstractStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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

/**
 * GrpcStubBeanPostProcessor
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp">Customizing Beans by Using a BeanPostProcessor</a>
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 * @since 2022-04-03
 */
public class GrpcStubBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcStubBeanPostProcessor.class);

    @Nullable
    private ConfigurableListableBeanFactory beanFactory;

    public GrpcStubBeanPostProcessor() {
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("GrpcStubBeanPostProcessor requires a ConfigurableListableBeanFactory: "
                + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        List<Field> fields = buildGrpcClientFields(bean.getClass());
        if (fields.isEmpty()) {
            return bean;
        }

        LOGGER.info("fields: {}", fields);

        Objects.requireNonNull(this.beanFactory, "beanFactory must not be null");
        final GrpcKitConfig grpcKitConfig;
        try {
            grpcKitConfig = this.beanFactory.getBean(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_CONFIG, GrpcKitConfig.class);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.error("bean {} must not be null", GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_CONFIG);
            throw e;
        }
        return bean;
    }

    private List<Field> buildGrpcClientFields(Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();
        Class<?> targetClass = clazz;
        do {
            final List<Field> currFields = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotations annotations = MergedAnnotations.from(field);
                MergedAnnotation<GrpcClient> annotation = annotations.get(GrpcClient.class);
                if (!annotation.isPresent()) {
                    return;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    LOGGER.warn("@GrpcClient annotation is not supported on static fields: " + field);
                    return;
                }
                if (!AbstractStub.class.isAssignableFrom(field.getType())) {
                    throw new GrpcKitException("@GrpcClient annotation is not supported on class: " + field.getType());
                }
                currFields.add(field);
            });

            fields.addAll(0, currFields);
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        return fields;
    }

}
