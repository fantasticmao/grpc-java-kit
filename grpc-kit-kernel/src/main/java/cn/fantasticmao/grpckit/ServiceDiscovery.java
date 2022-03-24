package cn.fantasticmao.grpckit;

import io.grpc.Attributes;
import io.grpc.NameResolver;

/**
 * Discover available service instances, implemented by using gRPC {@link io.grpc.NameResolver}
 * and {@link io.grpc.NameResolverProvider} plugins.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolver
 * @since 2022-03-13
 */
public abstract class ServiceDiscovery extends NameResolver {
    public static final Attributes.Key<Integer> KEY_WEIGHT = Attributes.Key.create("weight");
    public static final Attributes.Key<String> KEY_TAG = Attributes.Key.create("tag");

    /**
     * {@inheritDoc}
     */
    public abstract String getServiceAuthority();

    /**
     * {@inheritDoc}
     */
    public abstract void start(Listener2 listener);

    /**
     * {@inheritDoc}
     */
    public abstract void refresh();

    /**
     * {@inheritDoc}
     */
    public abstract void shutdown();

}
