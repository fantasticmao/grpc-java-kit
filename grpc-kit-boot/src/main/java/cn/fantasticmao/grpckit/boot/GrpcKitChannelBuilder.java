package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.ServiceURI;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.AbstractManagedChannelImplBuilder;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Objects;

/**
 * A builder for creating {@link ManagedChannel gRPC ManagedChannel} instances.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-21
 */
public class GrpcKitChannelBuilder extends AbstractManagedChannelImplBuilder<GrpcKitChannelBuilder> {
    private final ManagedChannelBuilder<?> managedChannelBuilder;

    private GrpcKitChannelBuilder(String appName, GrpcKitConfig config) {
        final String appGroup = config.getGrpc().getGroup();
        final String registry = Objects.requireNonNull(config.getNameResolver().getRegistry(),
            "nameResolver.registry must not be null");

        final String policy = config.getLoadBalancer().getPolicy();
        final ServiceURI serviceUri = ServiceURI.Factory.loadWith(URI.create(registry), appName, appGroup);
        this.managedChannelBuilder = ManagedChannelBuilder.forTarget(serviceUri.toTargetUri().toString())
            .userAgent(appName)
            .defaultLoadBalancingPolicy(ServiceLoadBalancer.Policy.of(policy).name);
    }

    public static GrpcKitChannelBuilder forConfig(String appName, @Nonnull GrpcKitConfig config) {
        if (appName == null || appName.isBlank()) {
            throw new IllegalArgumentException("application name must not be null or blank");
        }
        if (!ApplicationMetadata.NAME_PATTERN.matcher(appName).matches()) {
            throw new IllegalArgumentException("application name must match the pattern: "
                + ApplicationMetadata.NAME_PATTERN.pattern());
        }
        return new GrpcKitChannelBuilder(appName, config.validate());
    }

    @Override
    protected ManagedChannelBuilder<?> delegate() {
        return managedChannelBuilder;
    }
}
