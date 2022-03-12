package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.nameresolver.ServiceNameResolver;
import io.grpc.NameResolver;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * A ZooKeeper-based {@link NameResolver}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolver
 * @see <a href="https://zookeeper.apache.org/">Apache ZooKeeper</a>
 * @since 2021-07-31
 */
public class ZkNameResolver extends NameResolver implements ServiceNameResolver.Discovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkNameResolver.class);

    private final CuratorFramework zkClient;
    private final String path;

    public ZkNameResolver(@Nonnull String connectString, int sessionTimeout, @Nonnull String path) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5_000, 3);
        this.zkClient = CuratorFrameworkFactory.builder()
            .connectString(connectString)
            .sessionTimeoutMs(sessionTimeout)
            .retryPolicy(retryPolicy)
            .build();
        this.zkClient.start();
        try {
            this.zkClient.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Connect to ZooKeeper error", e);
            // TODO throw exception
            throw new RuntimeException(e);
        }

        this.path = path;
    }

    @Override
    public String getServiceAuthority() {
        return this.lookup(this.path);
    }

    @Override
    public void start(Listener2 listener) {
        // todo
    }

    @Override
    public void shutdown() {
        zkClient.close();
    }

    @Override
    public String lookup(String path) {
        final byte[] data;
        try {
            String root = "/grpc-java/service";
            String group = "/default";
            String fullPath = root + path + group;
            data = this.zkClient.getData().forPath(fullPath);
        } catch (Exception e) {
            LOGGER.error("ZooKeeper client error", e);
            return null;
        }
        // TODO load balance
        return new String(data, StandardCharsets.UTF_8);
    }
}
