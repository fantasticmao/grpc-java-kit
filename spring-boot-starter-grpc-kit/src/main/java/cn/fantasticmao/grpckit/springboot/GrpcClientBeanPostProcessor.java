package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.GrpcKitFactory;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import io.grpc.stub.AbstractStub;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * GrpcClientBeanPostProcessor
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp">Customizing Beans by Using a BeanPostProcessor</a>
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 * @since 2022-04-03
 */
public class GrpcClientBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public GrpcClientBeanPostProcessor() {
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        Objects.requireNonNull(this.applicationContext, "applicationContext must not be null");

        GrpcKitFactory grpcKitFactory = this.applicationContext.getBean(GrpcKitFactory.class);
        Objects.requireNonNull(grpcKitFactory, "bean 'grpcKitFactory' in applicationContext must not be null");

        ReflectionUtils.doWithFields(bean.getClass(), new Callback(bean, grpcKitFactory),
            filter -> filter.isAnnotationPresent(GrpcClient.class));
        return bean;
    }

    private static class Callback implements ReflectionUtils.FieldCallback {
        private final Object bean;
        private final GrpcKitFactory grpcKitFactory;

        public Callback(Object bean, GrpcKitFactory grpcKitFactory) {
            this.bean = bean;
            this.grpcKitFactory = grpcKitFactory;
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
