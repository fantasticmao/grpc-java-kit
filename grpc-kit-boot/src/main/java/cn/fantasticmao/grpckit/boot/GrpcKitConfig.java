package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.ServiceMetadata;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * The gRPC Java Kit configurations.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-08-04
 */
@Getter
@Setter
public class GrpcKitConfig {

    /**
     * Load and parse {@link GrpcKitConfig} from the specific file.
     *
     * @param path the config file path
     * @return a new {@link GrpcKitConfig} instance
     * @throws GrpcKitException errors during loading and parsing phases
     */
    public static GrpcKitConfig loadAndParse(@Nonnull String path) {
        Yaml yaml = new Yaml(new Constructor(GrpcKitConfig.class));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(path);
        Objects.requireNonNull(url, "File not found: " + path);

        try (InputStream input = url.openStream()) {
            GrpcKitConfig config = yaml.load(input);
            return config.validate();
        } catch (IOException | YAMLException e) {
            throw new GrpcKitException("Unable to load configurations from location: " + path, e);
        }
    }

    public GrpcKitConfig validate() throws IllegalArgumentException {
        if (nameResolver.getRegistry() == null || nameResolver.getRegistry().isBlank()) {
            throw new IllegalArgumentException("nameResolver.registry must not be null or blank");
        }
        return this;
    }

    private Grpc grpc = new Grpc();
    private NameResolver nameResolver = new NameResolver();
    private LoadBalancer loadBalancer = new LoadBalancer();

    @Getter
    @Setter
    public static class Grpc {
        private String group = "default";
        private Server server = new Server();
        private Client client = new Client();

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
