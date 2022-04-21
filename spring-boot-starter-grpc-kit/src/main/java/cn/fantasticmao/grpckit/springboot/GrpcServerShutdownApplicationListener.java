package cn.fantasticmao.grpckit.springboot;

import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import javax.annotation.Nonnull;

/**
 * An {@link ApplicationListener} for shutdown the gRPC {@link io.grpc.Server Server}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events">Standard and Custom Events</a>
 * @since 2022-04-13
 */
public class GrpcServerShutdownApplicationListener implements ApplicationListener<ContextClosedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerShutdownApplicationListener.class);

    public GrpcServerShutdownApplicationListener() {
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextClosedEvent event) {
        final ApplicationContext context = event.getApplicationContext();

        final Server grpcServer;
        try {
            grpcServer = context.getBean(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_SERVER, Server.class);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.debug("The gRPC server never has been started");
            throw e;
        }

        grpcServer.shutdown();
    }
}
