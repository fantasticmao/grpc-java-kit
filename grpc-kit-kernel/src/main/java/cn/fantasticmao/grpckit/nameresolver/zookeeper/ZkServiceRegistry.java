package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.ServiceRegistry;
import cn.fantasticmao.grpckit.support.GsonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * A ZooKeeper based {@link ServiceRegistry}.
 * <p>
 * Data model in ZooKeeper:
 * <pre>
 *                       grpc-java
 *                       /       \
 *                    app_1     app_2
 *                    /
 *                default(group)
 *                 /     \
 *             servers clients
 *             /     \
 * 192.168.1.1:8080  192.168.1.2:8080
 * </pre>
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
class ZkServiceRegistry extends ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistry.class);

    private final String servicePath;
    private final CuratorFramework zkClient;

    ZkServiceRegistry(URI serviceUri) {
        this.servicePath = serviceUri.getPath();

        String connectString = serviceUri.getAuthority();
        this.zkClient = ZkClientHolder.get(connectString);

        try {
            this.zkClient.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new GrpcKitException("Connect to ZooKeeper error, connect string: " + connectString, e);
        }
    }

    @Override
    public boolean doRegister(ServiceMetadata metadata) {
        final String path = this.servicePath;
        final Stat stat;
        try {
            stat = this.zkClient.checkExists().forPath(path);
        } catch (Exception e) {
            throw new GrpcKitException("Exists service error, path: " + this.servicePath, e);
        }

        if (stat != null) {
            throw new GrpcKitException("Already exists service, path: " + this.servicePath);
        }

        String metadataJson = GsonUtil.GSON.toJson(metadata);
        try {
            String createdPath = this.zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, metadataJson.getBytes(StandardCharsets.UTF_8));
            LOGGER.debug("Create new service for path: {}", createdPath);
            return true;
        } catch (Exception e) {
            throw new GrpcKitException("Create new service error, metadata: " + metadataJson, e);
        }
    }

    @Override
    public void shutdown() {
    }
}
