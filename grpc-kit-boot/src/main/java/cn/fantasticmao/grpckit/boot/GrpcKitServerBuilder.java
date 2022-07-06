package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.*;
import cn.fantasticmao.grpckit.support.Constant;
import cn.fantasticmao.grpckit.util.NetUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.internal.AbstractServerImplBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
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

    private GrpcKitServerBuilder(String appName, GrpcKitConfig config) {
        this.appName = appName;
        this.config = config;
        this.serverBuilder = ServerBuilder.forPort(config.getGrpc().getServer().getPort());
    }

    public static GrpcKitServerBuilder forConfig(String appName, @Nonnull GrpcKitConfig config) {
        if (appName == null || appName.isBlank()) {
            throw new IllegalArgumentException("application name must not be null or blank");
        }
        if (!ApplicationMetadata.NAME_PATTERN.matcher(appName).matches()) {
            throw new IllegalArgumentException("application name must match the pattern: "
                + ApplicationMetadata.NAME_PATTERN.pattern());
        }
        return new GrpcKitServerBuilder(appName, config.validate());
    }

    @Override
    protected ServerBuilder<?> delegate() {
        return this.serverBuilder;
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
        final String appGroup = config.getGrpc().getGroup();
        final int serverPort = config.getGrpc().getServer().getPort();
        final int serverWeight = config.getGrpc().getServer().getWeight();
        final String serverTag = config.getGrpc().getServer().getTag();
        final String registry = Objects.requireNonNull(config.getNameResolver().getRegistry(),
            "nameResolver.registry must not be null");

        final InetAddress localAddress;
        try {
            String preferInterface = this.config.getGrpc().getServer().getInterfaceName();
            localAddress = NetUtil.getLocalAddress(preferInterface);
        } catch (SocketException e) {
            throw new GrpcKitException("Get local address error", e);
        }

        final ServiceURI serviceUri = ServiceURILoader.loadWith(URI.create(registry), appName, appGroup);
        final ServiceMetadata metadata = new ServiceMetadata(localAddress, serverPort, serverWeight,
            serverTag, appName, Constant.VERSION);
        for (ServiceRegistryProvider provider : this.getAllServiceRegistries()) {
            // FIXME
            ServiceRegistry serviceRegistry = provider.newServiceRegistry(serviceUri.toTargetUri(),
                new InetSocketAddress(localAddress, serverPort));
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
