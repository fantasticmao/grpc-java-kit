package cn.fantasticmao.grpckit.springboot;

import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An {@link ApplicationListener} for shutdown the gRPC {@link Server Server}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events">Standard and Custom Events</a>
 * @since 2022-04-13
 */
public class GrpcServerShutdownApplicationListener implements ApplicationListener<ContextClosedEvent>,
    ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerShutdownApplicationListener.class);

    private final AtomicBoolean terminated;
    @Nullable
    private ApplicationContext applicationContext;

    public GrpcServerShutdownApplicationListener() {
        this.terminated = new AtomicBoolean(false);
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextClosedEvent event) {
        Objects.requireNonNull(this.applicationContext, "applicationContext must not be null");
        if (!this.terminated.compareAndSet(false, true)) {
            LOGGER.debug("The gRPC server has already been terminated");
            return;
        }

        final Server grpcServer;
        try {
            grpcServer = this.applicationContext.getBean(
                GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_SERVER, Server.class);
        } catch (NoSuchBeanDefinitionException ignored) {
            LOGGER.debug("The gRPC server never has been started");
            return;
        }

        grpcServer.shutdown();
    }
}
