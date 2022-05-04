package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import cn.fantasticmao.grpckit.boot.GrpcKitServerBuilder;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcService;
import cn.fantasticmao.grpckit.springboot.factory.GrpcKitServerBuilderFactory;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An {@link ApplicationListener} for start the gRPC {@link io.grpc.Server Server}.
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
        final String appName = this.getCurrentApplicationName(context);

        final List<ServerServiceDefinition> services = this.getGrpcServices(context);
        final GrpcKitServerBuilderFactory builderFactory = this.getGrpcKitServerBuilderFactory(context);
        final GrpcKitConfig config = this.getGrpcKitConfig(context);

        GrpcKitServerBuilder builder = GrpcKitServerBuilder.forConfig(appName, config);
        builder = builderFactory.maintain(builder, services);
        Server server = builder.build();

        try {
            server.start();
        } catch (IOException e) {
            throw new GrpcKitException("Start gRPC server error", e);
        }

        this.registerBeanForGrpcServer(context, server);
        this.publishGrpcServiceStartedEvent(context, event, services);
    }

    /**
     * By default, if no application name is set, "application" will be used.
     *
     * @see org.springframework.boot.context.ContextIdApplicationContextInitializer
     */
    private String getCurrentApplicationName(ApplicationContext context) {
        return context.getId();
    }

    private List<ServerServiceDefinition> getGrpcServices(ApplicationContext context) {
        Map<String, Object> grpcServiceBeans = context.getBeansWithAnnotation(GrpcService.class);
        return grpcServiceBeans.values().stream()
            .filter(bean -> {
                if (bean instanceof BindableService) {
                    return true;
                } else {
                    LOGGER.warn("@GrpcService annotation is not supported on the class: {}",
                        bean.getClass());
                    return false;
                }
            })
            .map(bean -> (BindableService) bean)
            .map(BindableService::bindService)
            .collect(Collectors.toList());
    }

    private GrpcKitServerBuilderFactory getGrpcKitServerBuilderFactory(ApplicationContext context) {
        try {
            return context.getBean(GrpcKitServerBuilderFactory.class);
        } catch (NoSuchBeanDefinitionException e) {
            return GrpcKitServerBuilderFactory.Default.INSTANCE;
        }
    }

    private GrpcKitConfig getGrpcKitConfig(ApplicationContext context) {
        return context.getBean(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_CONFIG, GrpcKitConfig.class);
    }

    private void registerBeanForGrpcServer(ConfigurableApplicationContext context, Server server) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_SERVER, server);
    }

    private void publishGrpcServiceStartedEvent(ApplicationContext context,
                                                ApplicationReadyEvent sourceEvent,
                                                List<ServerServiceDefinition> services) {
        List<String> serviceNames = services.stream()
            .map(service -> service.getServiceDescriptor().getName())
            .collect(Collectors.toList());
        ApplicationEvent event = new GrpcServerStartedEvent(sourceEvent, serviceNames);
        context.publishEvent(event);
    }
}
