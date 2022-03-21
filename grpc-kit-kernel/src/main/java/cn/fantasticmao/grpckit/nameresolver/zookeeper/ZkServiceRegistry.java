package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.Constant;
import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.ServiceRegistry;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * A ZooKeeper based {@link ServiceRegistry}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
class ZkServiceRegistry extends ServiceRegistry implements ZkServiceBased {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistry.class);

    private final String servicePath;
    private final CuratorFramework zkClient;

    ZkServiceRegistry(URI serviceUri) {
        this.servicePath = serviceUri.getPath();

        final String connectString = serviceUri.getAuthority();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5_000, 3);
        this.zkClient = CuratorFrameworkFactory.builder()
            .connectString(connectString)
            .retryPolicy(retryPolicy)
            .build();
        this.zkClient.start();

        try {
            this.zkClient.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new GrpcKitException("Connect to ZooKeeper error, connect string: " + connectString, e);
        }
    }

    @Override
    public boolean doRegister(ServiceMetadata metadata) {
        final String path = PATH_ROOT + this.servicePath;
        final Stat stat;
        try {
            stat = this.zkClient.checkExists().forPath(path);
        } catch (Exception e) {
            throw new GrpcKitException("Exists service node error, for path: " + this.servicePath, e);
        }

        if (stat != null) {
            throw new GrpcKitException("Already exists service node, for path: " + this.servicePath);
        }

        String metadataJson = Constant.GSON.toJson(metadata);
        try {
            String createdPath = this.zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, metadataJson.getBytes(StandardCharsets.UTF_8));
            LOGGER.info("Create new service node for path: {}", createdPath);
            return true;
        } catch (Exception e) {
            throw new GrpcKitException("Create service node error, for metadata: " + metadataJson, e);
        }
    }

    @Override
    public void close() {
        if (this.zkClient != null) {
            // FIXME close when server shutdown
            // this.zkClient.close();
        }
    }
}
