package cn.fantasticmao.grpckit.support;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceRegistry;
import cn.fantasticmao.grpckit.ServiceRegistryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
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
        ServiceLoader<ServiceRegistryProvider> providers
            = ServiceLoader.load(ServiceRegistryProvider.class);
        List<ServiceRegistryProvider> providerList = providers.stream()
            .map(ServiceLoader.Provider::get)
            .sorted()
            .collect(Collectors.toList());

        final InetAddress address;
        try {
            address = NetUtil.getLocalAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new GrpcKitException("Get local address error", e);
        }
        final InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        final URI uri = URI.create(serviceUri);

        for (ServiceRegistryProvider provider : providerList) {
            if (!provider.isAvailable()) {
                continue;
            }
            try (ServiceRegistry registry = provider.newServiceRegistry(uri)) {
                if (registry == null) {
                    continue;
                }

                String serviceName = uri.getPath();
                boolean result = registry.doRegister(serviceName, socketAddress);
                if (!result) {
                    LOGGER.error("Register service failed, name: {}", serviceName);
                }
            }
        }
    }
}
