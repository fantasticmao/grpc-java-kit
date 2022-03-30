package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Holder for ZooKeeper Client.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://zookeeper.apache.org/">Apache ZooKeeper</a>
 * @since 2022-03-22
 */
class ZkClientHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClientHolder.class);
    private static final String PATH_ROOT = "grpc-java";
    private static final ConcurrentHashMap<String, CuratorFramework> CLIENT_CACHE = new ConcurrentHashMap<>();

    public static CuratorFramework get(String connectString) {
        if (!CLIENT_CACHE.containsKey(connectString)) {
            synchronized (ZkClientHolder.class) {
                if (!CLIENT_CACHE.containsKey(connectString)) {
                    CuratorFramework client = CuratorFrameworkFactory.builder()
                        .namespace(PATH_ROOT)
                        .connectString(connectString)
                        .retryPolicy(new ExponentialBackoffRetry(5_000, 3))
                        .sessionTimeoutMs(15_000)
                        .build();
                    client.start();
                    CLIENT_CACHE.put(connectString, client);
                }
            }
        }
        return CLIENT_CACHE.get(connectString);
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            CLIENT_CACHE.forEach((connectString, client) -> {
                if (CuratorFrameworkState.STARTED == client.getState()) {
                    LOGGER.debug("Close ZooKeeper connection for connection string: {}", connectString);
                    client.close();
                }
            })
        ));
    }
}
