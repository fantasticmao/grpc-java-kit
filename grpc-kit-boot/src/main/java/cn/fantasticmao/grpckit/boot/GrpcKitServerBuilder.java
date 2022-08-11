package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.*;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.boot.factory.GrpcKitServerBuilderFactory;
import cn.fantasticmao.grpckit.boot.metadata.ApplicationNameValidator;
import cn.fantasticmao.grpckit.support.Constant;
import cn.fantasticmao.grpckit.util.NetUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.internal.AbstractServerImplBuilder;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * A builder for creating {@link io.grpc.Server gRPC Server} instances.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-21
 */
public class GrpcKitServerBuilder extends AbstractServerImplBuilder<GrpcKitServerBuilder> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcKitServerBuilder.class);

    private final String appName;
    private final GrpcKitConfig config;
    private final ServerBuilder<?> serverBuilder;

    private GrpcKitServerBuilder(String appName, @Nonnull GrpcKitConfig config) {
        this.appName = appName;
        this.config = config;
        this.serverBuilder = ServerBuilder.forPort(config.getServer().getPort());
    }

    public static GrpcKitServerBuilder forConfig(String appName, @Nonnull GrpcKitConfig config) {
        String registry = config.validate().getNameResolver().getRegistry();
        ApplicationNameValidator.validateWithRegistry(appName, registry);
        return new GrpcKitServerBuilder(appName, config.validate());
    }

    public GrpcKitServerBuilder customize(GrpcKitServerBuilderFactory factory) {
        return factory.customize(this);
    }

    @Override
    protected ServerBuilder<?> delegate() {
        return this.serverBuilder;
    }

    @Override
    public GrpcKitServerBuilder executor(@Nullable Executor executor) {
        if (executor != null) {
            Tags tags = Tags.of("app.name", appName, "group", config.getGroup());
            /*
             * Add executor metrics to the global registry.
             *
             * @see https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/9058ad6f40a75d15a70a69d7fe32ff2c19b05a00/instrumentation/micrometer/micrometer-1.5/javaagent/src/main/java/io/opentelemetry/javaagent/instrumentation/micrometer/v1_5/MetricsInstrumentation.java#L35
             */
            executor = ExecutorServiceMetrics.monitor(Metrics.globalRegistry, executor,
                "grpc_server", tags);
        }
        return super.executor(executor);
    }

    @Override
    public Server build() {
        final Server server = super.build();
        return new ForwardingServer(server) {
            @Override
            public Server start() throws IOException {
                Server _server = super.start();
                GrpcKitServerBuilder.this.register(_server);
                return _server;
            }
        };
    }

    private void register(Server server) {
        final String appGroup = config.getGroup();
        final int serverPort = server.getPort();
        final int serverWeight = config.getServer().getWeight();
        final String serverTag = config.getServer().getTag();
        final String registry = config.getNameResolver().getRegistry();

        final InetAddress localAddress;
        try {
            String preferInterface = this.config.getServer().getInterfaceName();
            localAddress = NetUtil.getLocalAddress(preferInterface);
        } catch (SocketException e) {
            throw new GrpcKitException("Get local address error", e);
        }

        final ServiceURI serviceUri = ServiceURI.Factory.loadWith(URI.create(registry), appName, appGroup);
        final InetSocketAddress address = new InetSocketAddress(localAddress, serverPort);
        final ServiceMetadata metadata = new ServiceMetadata(localAddress, serverPort, serverWeight,
            serverTag, appName, Constant.VERSION);
        for (ServiceRegistryProvider provider : this.getAllServiceRegistries()) {
            ServiceRegistry serviceRegistry = provider.newServiceRegistry(serviceUri, address);
            if (serviceRegistry == null) {
                continue;
            }
            boolean result = serviceRegistry.doRegister(metadata);
            if (!result) {
                LOGGER.error("Register service failed for URI: {}", serviceUri);
            }
        }
    }

    private List<ServiceRegistryProvider> getAllServiceRegistries() {
        ServiceLoader<ServiceRegistryProvider> providers
            = ServiceLoader.load(ServiceRegistryProvider.class);
        return providers.stream()
            .map(ServiceLoader.Provider::get)
            .filter(ServiceRegistryProvider::isAvailable)
            .sorted()
            .collect(Collectors.toList());
    }
}
