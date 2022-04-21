package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import io.grpc.stub.AbstractStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
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
public class GrpcStubBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcStubBeanPostProcessor.class);

    private ApplicationContext context;

    public GrpcStubBeanPostProcessor() {
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        Objects.requireNonNull(this.context, "applicationContext must not be null");

        final GrpcKitConfig grpcKitConfig;
        try {
            grpcKitConfig = this.context.getBean(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_CONFIG, GrpcKitConfig.class);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.error("bean {} in applicationContext must not be null",
                GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_CONFIG);
            throw e;
        }

        ReflectionUtils.doWithFields(bean.getClass(), new Callback(bean, grpcKitConfig),
            filter -> filter.isAnnotationPresent(GrpcClient.class));
        return bean;
    }

    private static class Callback implements ReflectionUtils.FieldCallback {
        private final Object bean;
        private final GrpcKitConfig grpcKitConfig;

        public Callback(Object bean, GrpcKitConfig grpcKitConfig) {
            this.bean = bean;
            this.grpcKitConfig = grpcKitConfig;
        }

        @Override
        public void doWith(@Nonnull Field stubField) throws IllegalArgumentException, IllegalAccessException {
            Class<?> stubFieldClass = stubField.getType();
            if (!AbstractStub.class.isAssignableFrom(stubFieldClass)) {
                String message = String.format("@GrpcClient %s in %s is not a standard gRPC Stub.",
                    stubField.getName(), stubFieldClass.getName());
                throw new GrpcKitException(message);
            }
            // TODO
            // grpcKitFactory.newStub(stubFieldClass, null);
        }
    }
}
