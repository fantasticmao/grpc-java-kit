package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.GrpcKitServerBuilder;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcService;
import cn.fantasticmao.grpckit.springboot.event.GrpcServerStartedEvent;
import cn.fantasticmao.grpckit.springboot.factory.GrpcKitServerBuilderFactory;
import cn.fantasticmao.grpckit.support.Constant;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GrpcServerContainer extends ApplicationBaseInfo implements SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerContainer.class);

    @Nullable
    private Server server;

    public GrpcServerContainer() {
    }

    @Override
    public void start() {
        final List<ServerServiceDefinition> services = this.getGrpcServices();
        if (services.isEmpty()) {
            LOGGER.debug("No need to start gRPC Server, because of no gRPC services");
            return;
        }

        this.server = this.createAndStartServer(services);
        this.awaitServerShutdown(server);
        this.publishGrpcServiceStartedEvent(server);
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

    /**
     * Get all services that annotated with {@link GrpcService @GrpcService} from Spring.
     *
     * @return gRPC service definitions
     */
    private List<ServerServiceDefinition> getGrpcServices() {
        Objects.requireNonNull(super.applicationContext, "applicationContext must not be null");
        Map<String, Object> grpcServiceBeans = super.applicationContext.getBeansWithAnnotation(GrpcService.class);
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

    @Nonnull
    private Server createAndStartServer(List<ServerServiceDefinition> services) {
        String appName = super.getCurrentAppName();
        GrpcKitConfig config = super.getGrpcKitConfig();
        GrpcKitServerBuilderFactory serverBuilderFactory = super.getGrpcKitServerBuilderFactory();

        LOGGER.info("Starting gRPC Server using gRPC-Java v{} in '{}' application",
            Constant.VERSION, appName);
        long startTime = System.nanoTime();
        GrpcKitServerBuilder builder = GrpcKitServerBuilder.forConfig(appName, config);
        builder = serverBuilderFactory.customize(builder, services);
        Server server = builder.build();

        try {
            server.start();
        } catch (IOException e) {
            throw new GrpcKitException("gRPC server started error", e);
        }

        long elapsedNanos = System.nanoTime() - startTime;
        LOGGER.info("Started gRPC Server on port {} in {} millis", server.getPort(),
            TimeUnit.NANOSECONDS.toMillis(elapsedNanos));
        return server;
    }

    private void awaitServerShutdown(@Nonnull Server createdServer) {
        final Thread currentThread = Thread.currentThread();
        Thread awaitThread = new Thread(() -> {
            try {
                createdServer.awaitTermination();
            } catch (InterruptedException e) {
                currentThread.interrupt();
            }
        }, "grpc-server-container");
        // The Java Virtual Machine exits when the only threads running are all daemon threads.
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    private void publishGrpcServiceStartedEvent(@Nonnull Server createdServer) {
        Objects.requireNonNull(super.applicationContext, "applicationContext must not be null");
        List<String> serviceNames = createdServer.getServices().stream()
            .map(service -> service.getServiceDescriptor().getName())
            .collect(Collectors.toList());
        ApplicationEvent event = new GrpcServerStartedEvent(createdServer, serviceNames);
        super.applicationContext.publishEvent(event);
    }
}
