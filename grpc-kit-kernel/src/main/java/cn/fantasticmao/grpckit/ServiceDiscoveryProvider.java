package cn.fantasticmao.grpckit;

import io.grpc.NameResolverProvider;

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
}
