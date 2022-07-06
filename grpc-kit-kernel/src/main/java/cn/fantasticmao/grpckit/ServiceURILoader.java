package cn.fantasticmao.grpckit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.Objects;
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
    @Nullable
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
    @Nullable
    ServiceURI from(URI targetUri);

    @Nonnull
    static ServiceURI loadWith(URI registryUri, String appName, String appGroup) {
        ServiceLoader<ServiceURILoader> serviceLoader = ServiceLoader.load(ServiceURILoader.class);
        return serviceLoader.stream()
            .map(ServiceLoader.Provider::get)
            .map(serviceURILoader -> serviceURILoader.with(registryUri, appName, appGroup))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Cannot find a suitable ServiceURILoader for %s:%s:%s", registryUri, appName, appGroup)));
    }

    @Nonnull
    static ServiceURI loadFrom(URI targetUri) {
        ServiceLoader<ServiceURILoader> serviceLoader = ServiceLoader.load(ServiceURILoader.class);
        return serviceLoader.stream()
            .map(ServiceLoader.Provider::get)
            .map(serviceURILoader -> serviceURILoader.from(targetUri))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Cannot find a suitable ServiceURILoader for %s", targetUri)));
    }
}
