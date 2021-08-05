package cn.fantasticmao.grpckit.common.constant;

/**
 * Constant
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021-08-04
 */
public interface Constant {
    String CONFIG_FILE_PATH = "grpc-kit.properties";

    enum ConfigKey {
        ZOOKEEPER_CONNECT_STRING("nameresolver.zookeeper.connect-string"),
        ZOOKEEPER_SESSION_TIMEOUT("nameresolver.zookeeper.session-timeout");

        public final String code;

        ConfigKey(String code) {
            this.code = code;
        }
    }
}
