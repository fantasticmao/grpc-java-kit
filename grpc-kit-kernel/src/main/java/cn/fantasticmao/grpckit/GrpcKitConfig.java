package cn.fantasticmao.grpckit;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Read configs from the specific file.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-08-04
 */
@Getter
@Setter
public final class GrpcKitConfig {

    /**
     * Load and parse {@link GrpcKitConfig} from the specified file path.
     *
     * @param path The config file path
     * @return A {@link GrpcKitConfig} object
     * @throws GrpcKitException Errors during loading and parsing phases
     */
    public static GrpcKitConfig loadAndParse(@Nonnull String path) {
        Objects.requireNonNull(path, "Path must not be null");
        Yaml yaml = new Yaml(new Constructor(GrpcKitConfig.class));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(path)) {
            if (input == null) {
                throw new FileNotFoundException("File not found: " + path);
            }
            return yaml.load(input);
        } catch (IOException | YAMLException e) {
            throw new GrpcKitException("Load configuration from: " + path + " error", e);
        }
    }

    public void checkNotNull() {
        if (name == null || name.isBlank()) {
            throw new NullPointerException("Name must not be null or blank");
        }
        if (nameResolver.getRegistry() == null || nameResolver.getRegistry().isBlank()) {
            throw new NullPointerException("Registry of nameResolver must not be null or blank");
        }
    }

    @Nullable
    private String name = null;
    private String group = "default";
    private Grpc grpc = new Grpc();
    private NameResolver nameResolver = new NameResolver();
    private LoadBalancer loadBalancer = new LoadBalancer();

    @Getter
    @Setter
    public static class Grpc {
        private Server server = new Server();
        private Stub stub = new Stub();

        @Getter
        @Setter
        public static class Server {
            private int port = 50051;
            private int weight = ServiceMetadata.DEFAULT_WEIGHT;
            private String tag = ServiceMetadata.DEFAULT_TAG;
            @Nullable
            private String interfaceName = null;
        }

        @Getter
        @Setter
        public static class Stub {
            private String tag = ServiceMetadata.DEFAULT_TAG;
            private int timeout = 2_000;
        }
    }

    @Getter
    @Setter
    public static class NameResolver {
        @Nullable
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
