package cn.fantasticmao.grpckit.nameresolver.zookeeper;

/**
 * Service discovery and registry based on ZooKeeper.
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
interface ZkServiceBased {
    String SCHEME = "zookeeper";

    String PATH_ROOT = "/grpc-java";
}
