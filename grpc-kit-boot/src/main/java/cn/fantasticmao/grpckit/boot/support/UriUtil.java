package cn.fantasticmao.grpckit.boot.support;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * An util class for {@link java.net.URI URI}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-19
 */
public interface UriUtil {

    /**
     * New URI for service discovery.
     *
     * @param registryUri  registry URI
     * @param appName      application name
     * @param serviceGroup service group
     * @see cn.fantasticmao.grpckit.ServiceDiscovery
     * @see cn.fantasticmao.grpckit.ServiceDiscoveryProvider
     */
    static URI newServiceUri(URI registryUri, String appName, String serviceGroup) {
        final String path = String.format("/%s/%s/servers", appName, serviceGroup);
        try {
            return new URI(registryUri.getScheme(), registryUri.getUserInfo(), registryUri.getHost(),
                registryUri.getPort(), path, registryUri.getQuery(), registryUri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * New URI for service registry.
     *
     * @param registryUri  registry URI
     * @param appName      application name
     * @param serviceGroup service group
     * @param address      host address
     * @param port         listening port
     * @see cn.fantasticmao.grpckit.ServiceRegistry
     * @see cn.fantasticmao.grpckit.ServiceRegistryProvider
     */
    static URI newServiceUri(URI registryUri, String appName, String serviceGroup, InetAddress address, int port) {
        final String path = String.format("/%s/%s/servers/%s:%d", appName, serviceGroup, address.getHostAddress(), port);
        try {
            return new URI(registryUri.getScheme(), registryUri.getUserInfo(), registryUri.getHost(),
                registryUri.getPort(), path, registryUri.getQuery(), registryUri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
