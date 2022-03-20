package cn.fantasticmao.grpckit.support;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A class related to {@link java.net.URI}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-19
 */
public interface UriUtil {

    /**
     * New URI for service discovery.
     *
     * @param registryUri registry URI
     * @param name        service name
     * @param group       service group
     * @see cn.fantasticmao.grpckit.ServiceDiscovery
     * @see cn.fantasticmao.grpckit.ServiceDiscoveryProvider
     */
    static URI newServiceUri(URI registryUri, String name, String group) {
        final String path = String.format("/%s/%s/server", name, group);
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
     * @param registryUri registry URI
     * @param name        service name
     * @param group       service group
     * @param address     local host address
     * @param port        listening port
     * @see cn.fantasticmao.grpckit.ServiceRegistry
     * @see cn.fantasticmao.grpckit.ServiceRegistryProvider
     */
    static URI newServiceUri(URI registryUri, String name, String group, InetAddress address, int port) {
        final String path = String.format("/%s/%s/server/%s:%d", name, group, address.getHostAddress(), port);
        try {
            return new URI(registryUri.getScheme(), registryUri.getUserInfo(), registryUri.getHost(),
                registryUri.getPort(), path, registryUri.getQuery(), registryUri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
