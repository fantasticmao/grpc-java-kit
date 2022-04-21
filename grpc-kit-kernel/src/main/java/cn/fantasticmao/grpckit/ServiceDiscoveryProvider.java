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
    @Override
    public abstract NameResolver newNameResolver(URI serviceUri, final NameResolver.Args args);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getDefaultScheme();

    /**
     * Implementations should not throw. If they do, it may interrupt class loading.
     * If exceptions may reasonably occur for implementation-specific reasons,
     * implementations should generally handle the exception gracefully
     * and return false from isAvailable().
     */
    @Override
    protected abstract boolean isAvailable();

    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract int priority();
}
