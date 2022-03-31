package cn.fantasticmao.grpckit;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

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
    private static volatile GrpcKitConfig instance;

    public static GrpcKitConfig getInstance() {
        if (instance == null) {
            synchronized (GrpcKitConfig.class) {
                if (instance == null) {
                    instance = load();
                }
            }
        }
        return instance;
    }

    private static GrpcKitConfig load() {
        Yaml yaml = new Yaml(new Constructor(GrpcKitConfig.class));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(Constant.CONFIG_FILE_PATH)) {
            return yaml.load(input);
        } catch (IOException | YAMLException e) {
            throw new GrpcKitException("Load config " + Constant.CONFIG_FILE_PATH + " error", e);
        }
    }

    @Nullable
    private String name = null;
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
            private int port = 50051;
            private int weight = 1;
            private String tag = "";
            @Nullable
            private String interfaceName = null;
        }

        @Getter
        @Setter
        public static class Client {
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
        private int maxFails = 1;
        private int failTimeout = 10_000;
    }
}
