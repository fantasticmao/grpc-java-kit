package cn.fantasticmao.grpckit;

import io.grpc.NameResolver;

/**
 * Discover available service instances, implemented by using gRPC {@link io.grpc.NameResolver NameResolver}
 * and {@link io.grpc.NameResolverProvider NameResolverProvider} plugins.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolver
 * @since 2022-03-13
 */
public abstract class ServiceDiscovery extends NameResolver {

    /**
     * Returns the authority used to authenticate connections to servers.  It <strong>must</strong> be
     * from a trusted source, because if the authority is tampered with, RPCs may be sent to the
     * attackers which may leak sensitive user data.
     *
     * <p>An implementation must generate it without blocking, typically in line, and
     * <strong>must</strong> keep it unchanged. {@code NameResolver}s created from the same factory
     * with the same argument must return the same authority.
     */
    @Override
    public abstract String getServiceAuthority();

    /**
     * Starts the resolution.
     *
     * @param listener used to receive updates on the target
     */
    @Override
    public abstract void start(Listener2 listener);

    /**
     * Re-resolve the name.
     *
     * <p>Can only be called after {@link #start} has been called.
     *
     * <p>This is only a hint. Implementation takes it as a signal but may not start resolution
     * immediately. It should never throw.
     *
     * <p>The default implementation is no-op.
     */
    @Override
    public abstract void refresh();

    /**
     * Stops the resolution. Updates to the Listener will stop.
     */
    @Override
    public abstract void shutdown();

}
