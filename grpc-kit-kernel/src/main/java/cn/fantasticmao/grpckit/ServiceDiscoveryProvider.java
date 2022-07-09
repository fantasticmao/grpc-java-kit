package cn.fantasticmao.grpckit;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import javax.annotation.Nullable;
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
     * Creates a {@link NameResolver} for the given target URI, or {@code null} if the given URI
     * cannot be resolved by this factory. The decision should be solely based on the scheme of the
     * URI.
     *
     * @param targetUri the target URI to be resolved, whose scheme must not be {@code null}
     * @param args      other information that may be useful
     */
    @Nullable
    public NameResolver newNameResolver(URI targetUri, final NameResolver.Args args) {
        ServiceURI serviceUri = ServiceURI.Factory.loadFrom(targetUri);
        return this.newNameResolver(serviceUri, args);
    }

    /**
     * Creates a {@link NameResolver} for the given service URI, or {@code null} if the given URI
     * cannot be resolved by this factory. The decision should be solely based on the scheme of the
     * URI.
     *
     * @param serviceUri the service URI to be resolved, whose scheme must not be {@code null}
     * @param args       other information that may be useful
     */
    @Nullable
    public abstract NameResolver newNameResolver(ServiceURI serviceUri, final NameResolver.Args args);

    /**
     * Returns the default scheme, which will be used to construct a URI when {@link
     * io.grpc.ManagedChannelBuilder#forTarget(String)} is given an authority string instead of a compliant
     * URI.
     */
    public abstract String getDefaultScheme();

    /**
     * Whether this provider is available for use, taking the current environment into consideration.
     * If false, no other methods are safe to be called.
     */
    protected abstract boolean isAvailable();

    /**
     * A priority, from 0 to 10 that this provider should be used, taking the current environment into
     * consideration. 5 should be considered the default, and then tweaked based on environment
     * detection. A priority of 0 does not imply that the provider wouldn't work; just that it should
     * be last in line.
     */
    protected abstract int priority();
}
