package cn.fantasticmao.grpckit;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

/**
 * A provider for {@link ServiceDiscovery}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolverProvider
 * @since 2022-03-13
 */
public abstract class ServiceDiscoveryProvider extends NameResolverProvider {
    protected static final int DEFAULT_PRIORITY = 5;

    /**
     * {@inheritDoc}
     */
    public abstract NameResolver newNameResolver(URI serviceUri, final NameResolver.Args args);

    /**
     * {@inheritDoc}
     */
    public abstract String getDefaultScheme();

    /**
     * {@inheritDoc}
     */
    protected abstract boolean isAvailable();

    /**
     * {@inheritDoc}
     */
    protected abstract int priority();
}
