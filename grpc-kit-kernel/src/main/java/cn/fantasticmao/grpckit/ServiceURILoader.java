package cn.fantasticmao.grpckit;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.ServiceLoader;

/**
 * A loader for {@link ServiceURI}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-07
 */
public interface ServiceURILoader {

    /**
     * The scheme which will be used to find compatible a {@link ServiceLoader}.
     *
     * @see #with(URI, String, String)
     */
    @Nonnull
    String getScheme();

    /**
     * Load a service URI with registry as well as application name and group.
     *
     * <p>Example of registry URIs:
     * <ul>
     *     <li>dns:///</li>
     *     <li>dns://8.8.8.8</li>
     *     <li>zookeeper://zk.example.com:2181</li>
     * </ul>
     *
     * @param registryUri registry URI
     * @param appName     application name
     * @param appGroup    application group
     * @return A URI to identify a gRPC service.
     */
    @Nonnull
    ServiceURI with(URI registryUri, String appName, String appGroup);

    /**
     * Load a service URI from a target URI.
     *
     * <p>Example of target URIs:
     * <ul>
     *     <li>dns:///example_service</li>
     *     <li>dns://8.8.8.8/example_service</li>
     *     <li>zookeeper://zk.example.com:2181/example_service/default</li>
     * </ul>
     *
     * @param targetUri target URI
     * @return A URI to identify a gRPC service.
     */
    @Nonnull
    ServiceURI from(URI targetUri);

    @Nonnull
    static ServiceURI loadWith(URI registryUri, String appName, String appGroup) {
        ServiceLoader<ServiceURILoader> serviceLoader = ServiceLoader.load(ServiceURILoader.class);
        return serviceLoader.stream()
            .map(ServiceLoader.Provider::get)
            .filter(serviceURILoader -> serviceURILoader.getScheme().equals(registryUri.getScheme()))
            .map(serviceURILoader -> serviceURILoader.with(registryUri, appName, appGroup))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Cannot find a suitable ServiceURILoader for %s:%s:%s", registryUri,
                    appName, appGroup)));
    }

    @Nonnull
    static ServiceURI loadFrom(URI targetUri) {
        ServiceLoader<ServiceURILoader> serviceLoader = ServiceLoader.load(ServiceURILoader.class);
        return serviceLoader.stream()
            .map(ServiceLoader.Provider::get)
            .filter(serviceURILoader -> serviceURILoader.getScheme().equals(targetUri.getScheme()))
            .map(serviceURILoader -> serviceURILoader.from(targetUri))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Cannot find a suitable ServiceURILoader for %s", targetUri)));
    }
}
