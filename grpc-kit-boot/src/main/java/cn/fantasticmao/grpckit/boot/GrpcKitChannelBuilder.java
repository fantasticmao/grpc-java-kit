package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.boot.support.UriUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.AbstractManagedChannelImplBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A builder for {@link ManagedChannel gRPC ManagedChannel} instances.
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
        final URI serviceUri = UriUtil.newServiceUri(URI.create(registry), appName, appGroup);
        this.managedChannelBuilder = ManagedChannelBuilder.forTarget(serviceUri.toString())
            .userAgent(appName)
            .defaultLoadBalancingPolicy(ServiceLoadBalancer.Policy.of(policy).name);
    }

    public static GrpcKitChannelBuilder forConfig(@Nullable String appName, @Nonnull GrpcKitConfig config) {
        if (appName == null || appName.isBlank()) {
            throw new IllegalArgumentException("application name must not be null or blank");
        }
        if (!Pattern.compile("[\\w]+").matcher(appName).matches()) {
            throw new IllegalArgumentException("application name must match the regular expression: [\\w]+");
        }
        return new GrpcKitChannelBuilder(appName, config.validate());
    }

    @Override
    protected ManagedChannelBuilder<?> delegate() {
        return managedChannelBuilder;
    }
}
