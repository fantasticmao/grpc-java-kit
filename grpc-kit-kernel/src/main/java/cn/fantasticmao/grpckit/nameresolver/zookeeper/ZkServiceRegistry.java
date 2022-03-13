package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceRegistry;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
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

    private final CuratorFramework zkClient;

    ZkServiceRegistry(URI targetUri) {
        final String connectString = targetUri.getAuthority();

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5_000, 3);
        this.zkClient = CuratorFrameworkFactory.builder()
            .connectString(connectString)
            .retryPolicy(retryPolicy)
            .build();
        this.zkClient.start();
    }

    @Override
    public boolean doRegister(String serviceName, InetSocketAddress address) {
        try {
            this.zkClient.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new GrpcKitException("Connect to ZooKeeper error", e);
        }

        final String host = address.getAddress().getHostAddress();
        final int port = address.getPort();
        final String path = newServerNodePath(serviceName, host, port);
        final Stat stat;
        try {
            stat = this.zkClient.checkExists().forPath(path);
        } catch (Exception e) {
            throw new GrpcKitException("Exists server node error", e);
        }

        if (stat != null) {
            String message = String.format("Server node already exists, name: %s, host: %s, port: %d",
                serviceName, host, port);
            throw new GrpcKitException(message);
        }

        try {
            String createdPath = this.zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, null);
            LOGGER.info("Create new server node for path: {}", createdPath);
            return true;
        } catch (Exception e) {
            throw new GrpcKitException("Create server node error", e);
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
