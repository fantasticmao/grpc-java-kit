package cn.fantasticmao.grpckit.support;

import cn.fantasticmao.grpckit.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * A class related to {@link io.grpc.Server}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public interface ServerBuddy {
    Logger LOGGER = LoggerFactory.getLogger(ServerBuddy.class);

    static void registerService(String serviceUri, int port) {
        ServiceLoader<ServiceRegistry.Provider> providers
            = ServiceLoader.load(ServiceRegistry.Provider.class);
        List<ServiceRegistry.Provider> providerList = providers.stream()
            .map(ServiceLoader.Provider::get)
            .sorted()
            .collect(Collectors.toList());

        URI uri = URI.create(serviceUri);
        // FIXME
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
        for (ServiceRegistry.Provider provider : providerList) {
            if (!provider.isAvailable()) {
                continue;
            }
            try (ServiceRegistry registry = provider.newServiceRegistry(uri)) {
                if (registry == null) {
                    continue;
                }

                String serviceName = uri.getPath();
                boolean result = registry.doRegister(serviceName, address);
                if (!result) {
                    LOGGER.error("Register service failed, name: {}", serviceName);
                }
            }
        }
    }
}
