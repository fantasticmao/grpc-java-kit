package cn.fantasticmao.grpckit;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.ServiceLoader;

/**
 * A URI associated with registryUri as well as application name and group, which will be
 * used to identify a gRPC service in service discovery and registration.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-06
 */
public abstract class ServiceURI {
    protected final URI registryUri;
    protected final String appName;
    protected final String appGroup;

    protected ServiceURI(URI registryUri, String appName, String appGroup) {
        this.registryUri = registryUri;
        this.appName = appName;
        this.appGroup = appGroup;
    }

    public abstract URI toTargetUri();

    /**
     * A factory for {@link ServiceURI}.
     */
    public interface Factory {
        /**
         * The scheme which will be used to find compatible a {@link ServiceLoader}.
         *
         * @see #with(URI, String, String)
         */
        @Nonnull
        String getScheme();

        /**
         * Create a service URI with registry as well as application name and group.
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
         * Create a service URI from a target URI.
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
            ServiceLoader<ServiceURI.Factory> serviceLoader = ServiceLoader.load(ServiceURI.Factory.class);
            return serviceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(factory -> factory.getScheme().equals(registryUri.getScheme()))
                .map(factory -> factory.with(registryUri, appName, appGroup))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("Cannot find a suitable ServiceURI.Factory for %s:%s:%s", registryUri,
                        appName, appGroup)));
        }

        @Nonnull
        static ServiceURI loadFrom(URI targetUri) {
            ServiceLoader<ServiceURI.Factory> serviceLoader = ServiceLoader.load(ServiceURI.Factory.class);
            return serviceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(factory -> factory.getScheme().equals(targetUri.getScheme()))
                .map(factory -> factory.from(targetUri))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("Cannot find a suitable ServiceURI.Factory for %s", targetUri)));
        }
    }
}
