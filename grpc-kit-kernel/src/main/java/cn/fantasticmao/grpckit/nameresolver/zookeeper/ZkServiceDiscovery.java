package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.GrpcKitConfig;
import cn.fantasticmao.grpckit.GrpcKitConfigKey;
import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceDiscovery;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A ZooKeeper based {@link ServiceDiscovery}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-07-31
 */
class ZkServiceDiscovery extends ServiceDiscovery implements ZkServiceBased {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);

    private final String connectString;
    private final String serviceName;
    private final CuratorFramework zkClient;

    ZkServiceDiscovery(URI targetUri, NameResolver.Args args) {
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
            throw new GrpcKitException("Connect to ZooKeeper error", e);
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
        if (this.zkClient != null) {
            this.zkClient.close();
        }
    }

    @Override
    protected List<InetSocketAddress> lookup(String serviceName) {
        final String path = getServerPath(serviceName);
        final List<String> serverList;
        try {
            serverList = this.zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            throw new GrpcKitException("Get server list error", e);
        }
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
    }
}
