package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import javax.annotation.Nonnull;

/**
 * ZooKeeper based operations.
 * <p>
 * Data model in ZooKeeper:
 * <pre>
 *                      grpc-java
 *                      /       \
 *                 service-1  service-2
 *                   /
 *               default(group)
 *                /    \
 *             server client
 *             /    \
 * 192.168.1.1:8080 192.168.1.2:8080
 * </pre>
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://zookeeper.apache.org/">Apache ZooKeeper</a>
 * @since 2022-03-13
 */
public interface ZkServiceBased {
    String SCHEME = "zookeeper";
    String PATH_ROOT = "grpc-java";
    String PATH_SERVER = "server";
    String PATH_CLIENT = "client";

    default String getServerPath(@Nonnull String serviceName, @Nonnull String group) {
        return String.format("/%s/%s/%s/%s", PATH_ROOT, serviceName, group, PATH_SERVER);
    }

    default String newServerNodePath(@Nonnull String serviceName, @Nonnull String group,
                                     @Nonnull String host, int port) {
        return String.format("/%s/%s/%s/%s/%s:%d", PATH_ROOT, serviceName, group, PATH_SERVER, host, port);
    }

    default String getClientPath(@Nonnull String serviceName, @Nonnull String group) {
        return String.format("/%s/%s/%s/%s", PATH_ROOT, serviceName, group, PATH_CLIENT);
    }
}
