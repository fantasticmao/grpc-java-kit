package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitFactory;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcService;
import io.grpc.BindableService;
import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An {@link ApplicationListener} for start the gRPC {@link Server Server}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events">Standard and Custom Events</a>
 * @since 2022-04-13
 */
public class GrpcServerStartApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerStartApplicationListener.class);

    public GrpcServerStartApplicationListener() {
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
        final ConfigurableApplicationContext context = event.getApplicationContext();
        Set<Map.Entry<String, Object>> grpcServiceBeans = context.getBeansWithAnnotation(GrpcService.class)
            .entrySet().stream()
            .filter(entry -> {
                if (entry.getValue() instanceof BindableService) {
                    return true;
                } else {
                    LOGGER.warn("@GrpcService is not valid for {}", entry.getValue().getClass().getName());
                    return false;
                }
            })
            .collect(Collectors.toSet());
        if (grpcServiceBeans.isEmpty()) {
            return;
        }

        Set<String> grpcServiceNames = grpcServiceBeans.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        Set<BindableService> grpcServices = grpcServiceBeans.stream()
            .map(Map.Entry::getValue)
            .map(obj -> (BindableService) obj)
            .collect(Collectors.toSet());

        final GrpcKitFactory grpcKitFactory;
        try {
            grpcKitFactory = context.getBean(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_FACTORY, GrpcKitFactory.class);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.error("bean {} in applicationContext must not be null",
                GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_FACTORY);
            throw e;
        }

        // FIXME
        Server grpcServer = grpcKitFactory.newAndStartServer("", grpcServices);
        if (grpcServer != null) {
            this.registerBeanForGrpcServer(context, grpcServer);
            this.publishGrpcServiceStartedEvent(context, event, grpcServiceNames);
        }
    }

    private void registerBeanForGrpcServer(ConfigurableApplicationContext context, Server grpcServer) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_SERVER, grpcServer);
    }

    private void publishGrpcServiceStartedEvent(ConfigurableApplicationContext context,
                                                ApplicationReadyEvent sourceEvent, Set<String> serviceNames) {
        ApplicationEvent event = new GrpcServerStartedEvent(sourceEvent, serviceNames);
        context.publishEvent(event);
    }
}
