package cn.fantasticmao.grpckit;

/**
 * Keys for {@link GrpcKitConfig}.
 *
 * @author fantasticmao
 * @since 1.39.0
 * @since 2022/3/7
 */
public enum GrpcKitConfigKey {
    GRPC_SERVER_PORT("grpc.server.port"),
    GRPC_CLIENT_TIMEOUT("grpc.client.timeout"),
    ZOOKEEPER_CONNECT_STRING("nameresolver.zookeeper.connect-string");

    public final String code;

    GrpcKitConfigKey(String code) {
        this.code = code;
    }
}
