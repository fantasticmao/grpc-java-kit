package cn.fantasticmao.grpckit;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Read configs from the specified file.
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
        } catch (IOException e) {
            throw new GrpcKitException("Load " + Constant.CONFIG_FILE_PATH + " config error", e);
        }
    }

    private Grpc grpc = new Grpc();
    private NameResolver nameResolver = new NameResolver();

    @Getter
    @Setter
    public static class Grpc {
        private String group = "default";
        private Server server = new Server();
        private Client client = new Client();

        @Getter
        @Setter
        public static class Server {
            private Integer port = 50051;
            private Integer weight = 100;
            private String tag = "";
            private String interfaceName = "";
        }

        @Getter
        @Setter
        public static class Client {
            private Integer timeout = 5_000;
        }
    }

    @Getter
    @Setter
    public static class NameResolver {
        private Zookeeper zookeeper = new Zookeeper();

        @Getter
        @Setter
        public static class Zookeeper {
            private String connectString = "";
        }
    }
}
