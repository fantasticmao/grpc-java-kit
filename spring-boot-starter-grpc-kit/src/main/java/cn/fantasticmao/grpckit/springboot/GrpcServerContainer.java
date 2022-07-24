package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.boot.GrpcKitServerBuilder;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcService;
import cn.fantasticmao.grpckit.springboot.event.GrpcServerStartedEvent;
import cn.fantasticmao.grpckit.springboot.factory.GrpcKitServerBuilderFactory;
import cn.fantasticmao.grpckit.support.Constant;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.SmartLifecycle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A container managed by Spring used to start and stop the gRPC {@link io.grpc.Server}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle-processor">Startup and Shutdown Callbacks</a>
 * @see org.springframework.boot.web.context.WebServerGracefulShutdownLifecycle
 * @since 2022-07-20
 */
public class GrpcServerContainer implements SmartLifecycle, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerContainer.class);

    @Nullable
    private Server server;
    @Nullable
    private ApplicationContext applicationContext;

    public GrpcServerContainer() {
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void start() {
        Objects.requireNonNull(applicationContext, "applicationContext must not be null");

        final List<ServerServiceDefinition> services = this.getGrpcServices(applicationContext);
        if (services.isEmpty()) {
            LOGGER.debug("No need to start gRPC Server, because of no gRPC services");
            return;
        }

        final GrpcKitConfig config = applicationContext.getBean(GrpcKitConfig.class).validate();
        final String appName = this.getCurrentApplicationName(applicationContext);
        final GrpcKitServerBuilderFactory factory = this.getGrpcKitServerBuilderFactory(applicationContext);

        LOGGER.info("Starting gRPC Server using gRPC-Java v{} in '{}' application",
            Constant.VERSION, appName);
        long startTime = System.nanoTime();
        this.server = this.createAndStartServer(config, appName, services, factory);
        long elapsedNanos = System.nanoTime() - startTime;
        LOGGER.info("Started gRPC Server on port {} in {} millis", server.getPort(),
            TimeUnit.NANOSECONDS.toMillis(elapsedNanos));

        // The Java Virtual Machine exits when the only threads running are all daemon threads.
        final Thread currentThread = Thread.currentThread();
        Thread awaitThread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                currentThread.interrupt();
            }
        }, "grpc-server-container");
        awaitThread.setDaemon(false);
        awaitThread.start();

        this.publishGrpcServiceStartedEvent(applicationContext, server);
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    @Override
    public boolean isRunning() {
        return server != null && !server.isShutdown();
    }

    @Nonnull
    private Server createAndStartServer(GrpcKitConfig config, String appName,
                                        List<ServerServiceDefinition> services,
                                        GrpcKitServerBuilderFactory factory) {
        GrpcKitServerBuilder builder = GrpcKitServerBuilder.forConfig(appName, config);
        builder = factory.customize(builder, services);
        Server server = builder.build();

        try {
            server.start();
        } catch (IOException e) {
            throw new GrpcKitException("gRPC server started error", e);
        }
        return server;
    }

    /**
     * By default, if no application name is set, "application" will be used.
     *
     * @see org.springframework.boot.context.ContextIdApplicationContextInitializer
     */
    private String getCurrentApplicationName(ApplicationContext context) {
        return context.getId();
    }

    /**
     * Get all services that annotated with {@link GrpcService @GrpcService} from Spring.
     *
     * @param context application context in Spring
     * @return gRPC service definitions
     */
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

    /**
     * Get the {@link GrpcKitServerBuilderFactory} from Spring, if not present,
     * use the {@link GrpcKitServerBuilderFactory.Default default factory}.
     *
     * @param context application context
     * @return factory used to build gRPC server.
     */
    private GrpcKitServerBuilderFactory getGrpcKitServerBuilderFactory(ApplicationContext context) {
        try {
            return context.getBean(GrpcKitServerBuilderFactory.class);
        } catch (NoSuchBeanDefinitionException e) {
            return GrpcKitServerBuilderFactory.Default.INSTANCE;
        }
    }

    private void publishGrpcServiceStartedEvent(ApplicationContext context, Server server) {
        List<String> serviceNames = server.getServices().stream()
            .map(service -> service.getServiceDescriptor().getName())
            .collect(Collectors.toList());
        ApplicationEvent event = new GrpcServerStartedEvent(server, serviceNames);
        context.publishEvent(event);
    }
}
