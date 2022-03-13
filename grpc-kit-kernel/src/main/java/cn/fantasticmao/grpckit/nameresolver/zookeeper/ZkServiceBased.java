package cn.fantasticmao.grpckit.nameresolver.zookeeper;

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
    String PATH_ROOT = "/grpc-java";
    String PATH_GROUP_SERVER = "/default/server";
    String PATH_GROUP_CLIENT = "/default/client";

    default String getServerPath(String serviceName) {
        return PATH_ROOT + serviceName + PATH_GROUP_SERVER;
    }

    default String newServerNodePath(String serviceName, String host, int port) {
        return PATH_ROOT + serviceName + PATH_GROUP_SERVER + "/" + host + ":" + port;
    }

    default String getClientPath(String serviceName) {
        return PATH_ROOT + serviceName + PATH_GROUP_CLIENT;
    }
}
