package cn.fantasticmao.grpckit;

import io.grpc.NameResolver;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Discover available service instances, implemented by using gRPC plugins.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolver
 * @since 2022-03-13
 */
public abstract class ServiceDiscovery extends NameResolver {

    /**
     * Discover available service instances
     *
     * @param serviceName service name
     * @return service node list
     */
    protected abstract List<InetSocketAddress> lookup(String serviceName);

}
