package cn.fantasticmao.grpckit;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * A class related to gRPC service.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public interface ServiceBuddy {
    Logger LOGGER = LoggerFactory.getLogger(ServiceBuddy.class);

    /**
     * Get name of gRPC service.
     *
     * @param service gRPC service
     */
    static String getServiceName(BindableService service) {
        ServerServiceDefinition serviceDefinition = service.bindService();
        return serviceDefinition.getServiceDescriptor().getName();
    }

    /**
     * Register service instance for the given URI.
     * <p>
     * Example URIs:
     * <ul>
     *     <li>zookeeper://localhost:2181/service_name/default/server/192.168.1.1:8080</li>
     *     <li>consul://localhost:8500/service_name/default/server/192.168.1.1:8080</li>
     * </ul>
     *
     * @param serviceUri service URI
     * @param metadata   service metadata
     */
    static void registerService(URI serviceUri, ServiceMetadata metadata) {
        ServiceLoader<ServiceRegistryProvider> providers
            = ServiceLoader.load(ServiceRegistryProvider.class);
        List<ServiceRegistryProvider> providerList = providers.stream()
            .map(ServiceLoader.Provider::get)
            .sorted()
            .collect(Collectors.toList());

        for (ServiceRegistryProvider provider : providerList) {
            if (!provider.isAvailable()) {
                continue;
            }
            try (ServiceRegistry registry = provider.newServiceRegistry(serviceUri)) {
                if (registry == null) {
                    continue;
                }

                String serviceName = serviceUri.getPath();
                boolean result = registry.doRegister(metadata);
                if (!result) {
                    LOGGER.error("Register service failed, name: {}", serviceName);
                }
            }
        }
    }
}
