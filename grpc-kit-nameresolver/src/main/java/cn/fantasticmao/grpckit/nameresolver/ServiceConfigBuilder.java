package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolver;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * ServiceConfig
 *
 * @author maomao
 * @version 1.39.0
 * @see io.grpc.ManagedChannelBuilder#defaultServiceConfig(Map)
 * @since 2021-08-03
 */
public class ServiceConfigBuilder {
    private final Map<String, String> serviceConfig;

    public static final String ZOOKEEPER_ADDRESS = "ZooKeeperAddress";

    public ServiceConfigBuilder() {
        serviceConfig = new HashMap<>();
    }

    /**
     * Create a service config for {@link ZooKeeperNameResolverProvider#newNameResolver(URI, NameResolver.Args)}.
     *
     * @param hosts ZooKeeper address list
     * @return ServiceConfigBuilder
     */
    public static ServiceConfigBuilder forZooKeeper(String... hosts) {
        ServiceConfigBuilder builder = new ServiceConfigBuilder();
        builder.serviceConfig.put(ZOOKEEPER_ADDRESS, String.join(",", hosts));
        return builder;
    }

    public static ServiceConfigBuilder forConsul(String... hosts) {
        // TODO
        ServiceConfigBuilder builder = new ServiceConfigBuilder();
        return builder;
    }

    public Map<String, ?> build() {
        return serviceConfig;
    }
}
