package cn.fantasticmao.grpckit.boot.config;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.ServiceMetadata;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * Configurations used in RPC Java Kit.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-08-04
 */
@Getter
@Setter
public class GrpcKitConfig {

    public GrpcKitConfig validate() throws IllegalArgumentException {
        if (nameResolver.getRegistry() == null || nameResolver.getRegistry().isBlank()) {
            throw new IllegalArgumentException("nameResolver.registry must not be null or blank");
        }
        return this;
    }

    private String group = "default";
    private Server server = new Server();
    private Client client = new Client();
    private NameResolver nameResolver = new NameResolver();
    private LoadBalancer loadBalancer = new LoadBalancer();

    @Getter
    @Setter
    public static class Server {
        private int port = ServiceMetadata.DEFAULT_PORT;
        private int weight = ServiceMetadata.DEFAULT_WEIGHT;
        private String tag = ServiceMetadata.DEFAULT_TAG;
        @Nullable
        private String interfaceName = null;
    }

    @Getter
    @Setter
    public static class Client {
        private String tag = ServiceMetadata.DEFAULT_TAG;
        private int timeout = 2_000;
    }

    @Getter
    @Setter
    public static class NameResolver {
        private String registry = null;
    }

    @Getter
    @Setter
    public static class LoadBalancer {
        private String policy = ServiceLoadBalancer.Policy.WEIGHTED_RANDOM.name;
        private int maxFails = 1;
        private int failTimeout = 10_000;
    }
}
