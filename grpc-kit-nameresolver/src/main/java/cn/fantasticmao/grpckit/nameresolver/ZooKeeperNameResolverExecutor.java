package cn.fantasticmao.grpckit.nameresolver;

import cn.fantasticmao.grpckit.common.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.common.constant.Constant;
import com.google.common.base.Preconditions;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ZooKeeperNameResolverExecutor
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021/8/5
 */
public class ZooKeeperNameResolverExecutor implements ServiceNameResolver.Registry, ServiceNameResolver.Discovery {
    private static volatile ZooKeeperNameResolverExecutor instance;

    private static final Logger LOGGER = Logger.getLogger(ZooKeeperNameResolverExecutor.class.getName());
    private final CuratorFramework client;

    public static ZooKeeperNameResolverExecutor getInstance() {
        if (instance == null) {
            synchronized (ZooKeeperNameResolverExecutor.class) {
                if (instance == null) {
                    instance = new ZooKeeperNameResolverExecutor();
                }
            }
        }
        return instance;
    }

    private ZooKeeperNameResolverExecutor() {
        String connectString = GrpcKitConfig.getInstance().getValue(Constant.ConfigKey.ZOOKEEPER_CONNECT_STRING);
        Preconditions.checkNotNull(connectString, "ZooKeeper connect string can not be null");

        int sessionTimeout;
        int defaultSessionTimeout = 5_000;
        String sessionTimeoutStr = GrpcKitConfig.getInstance().getValue(Constant.ConfigKey.ZOOKEEPER_SESSION_TIMEOUT,
            String.valueOf(defaultSessionTimeout));
        try {
            sessionTimeout = Integer.parseInt(sessionTimeoutStr);
        } catch (NumberFormatException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "ZooKeeper session timeout number format error, fallback to " +
                    defaultSessionTimeout + " ms", e);
            }
            sessionTimeout = defaultSessionTimeout;
        }

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5_000, 3);
        this.client = CuratorFrameworkFactory.builder()
            .connectString(connectString)
            .sessionTimeoutMs(sessionTimeout)
            .retryPolicy(retryPolicy)
            .build();

        client.start();
        try {
            client.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Connect to ZooKeeper error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String lookup(String path) throws Exception {
        byte[] bytes = this.client.getData()
            .forPath(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void doRegistry(String path, String data) throws Exception {
        this.client.create()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.EPHEMERAL)
            .forPath(path, data.getBytes(StandardCharsets.UTF_8));
    }
}
