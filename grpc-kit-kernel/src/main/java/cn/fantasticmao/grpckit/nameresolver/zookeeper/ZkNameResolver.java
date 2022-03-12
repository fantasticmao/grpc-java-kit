package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.common.GrpcKitConfig;
import cn.fantasticmao.grpckit.common.GrpcKitConfigKey;
import cn.fantasticmao.grpckit.nameresolver.ServiceNameResolver;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private final String connectString;
    private final String serviceName;
    private final CuratorFramework zkClient;

    public ZkNameResolver(URI targetUri, NameResolver.Args args) {
        this.connectString = targetUri.getAuthority();
        this.serviceName = targetUri.getPath();

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5_000, 3);
        this.zkClient = CuratorFrameworkFactory.builder()
            .connectString(connectString)
            .retryPolicy(retryPolicy)
            .build();
        this.zkClient.start();
    }

    @Override
    public String getServiceAuthority() {
        return this.connectString;
    }

    @Override
    public void start(Listener2 listener) {
        try {
            this.zkClient.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Connect to ZooKeeper error", e);
            // TODO throw exception
            throw new RuntimeException(e);
        }

        List<EquivalentAddressGroup> servers = this.lookup(serviceName).stream()
            .map(EquivalentAddressGroup::new)
            .collect(Collectors.toList());
        ResolutionResult result = ResolutionResult.newBuilder()
            .setAddresses(servers)
            .build();
        listener.onResult(result);
    }

    @Override
    public void shutdown() {
        zkClient.close();
    }

    @Override
    public List<InetSocketAddress> lookup(String serviceName) {
        try {
            String root = "/grpc-java";
            String group = "/server/default";
            String path = root + serviceName + group;
            List<String> serverList = this.zkClient.getChildren().forPath(path);
            return serverList.stream()
                .map(address -> {
                    String[] authorities = address.split(":");
                    final String host = authorities[0];
                    int port;
                    if (authorities.length > 1) {
                        try {
                            port = Integer.parseInt(authorities[1]);
                        } catch (NumberFormatException e) {
                            // falling back to default port
                            port = GrpcKitConfig.getInstance().getIntValue(GrpcKitConfigKey.GRPC_SERVER_PORT, 50051);
                            LOGGER.warn("Parse port in address: {} error, falling back to: {}", address, port, e);
                        }
                    } else {
                        // use default port
                        port = GrpcKitConfig.getInstance().getIntValue(GrpcKitConfigKey.GRPC_SERVER_PORT, 50051);
                    }
                    return new InetSocketAddress(host, port);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("ZooKeeper client error", e);
            return Collections.emptyList();
        }
    }
}
