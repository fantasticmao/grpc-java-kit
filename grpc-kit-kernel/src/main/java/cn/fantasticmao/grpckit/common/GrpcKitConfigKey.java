package cn.fantasticmao.grpckit.common;

/**
 * GrpcKitConfigKey
 *
 * @author fantasticmao
 * @since 1.39.0
 * @since 2022/3/7
 */
public enum GrpcKitConfigKey {
    ZOOKEEPER_CONNECT_STRING("nameresolver.zookeeper.connect-string");

    public final String code;

    GrpcKitConfigKey(String code) {
        this.code = code;
    }
}
