package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceDiscovery;
import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.util.GsonUtil;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A ZooKeeper based {@link ServiceDiscovery}.
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
 * @since 2021-07-31
 */
class ZkServiceDiscovery extends ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);

    private final String authority;
    private final String servicePath;
    private final CuratorFramework zkClient;

    private boolean resolving = false;
    private NameResolver.Listener2 listener;

    ZkServiceDiscovery(String zkAuthority, String connectString, String servicePath) {
        this.authority = zkAuthority;
        this.servicePath = servicePath;
        this.zkClient = ZkClientHolder.get(connectString);

        try {
            this.zkClient.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new GrpcKitException("Connect to ZooKeeper error, connect string: " + connectString, e);
        }
    }

    @Override
    public String getServiceAuthority() {
        return this.authority;
    }

    @Override
    public void start(Listener2 listener) {
        if (this.listener != null) {
            LOGGER.warn("Already started {}", this.getClass().getName());
            return;
        }
        this.listener = listener;
        this.resolve();
    }

    @Override
    public void refresh() {
        if (this.listener == null) {
            LOGGER.warn("Not started {}", this.getClass().getName());
            return;
        }
        this.resolve();
    }

    @Override
    public void shutdown() {
    }

    private void resolve() {
        if (this.resolving) {
            LOGGER.warn("Already resolved {}", this.getClass().getName());
            return;
        }

        this.resolving = true;
        try {
            this.lookUp();
        } finally {
            this.resolving = false;
        }
    }

    private void lookUp() {
        final String path = this.servicePath;
        final List<String> serverList;
        try {
            serverList = this.zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            throw new GrpcKitException("Get server list error, for path: " + path, e);
        }

        final List<ServiceMetadata> serviceMetadataList = new ArrayList<>(serverList.size());
        for (String serverPath : serverList) {
            final String metadataJson;
            try {
                byte[] bytes = this.zkClient.getData().forPath(path + "/" + serverPath);
                metadataJson = new String(bytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new GrpcKitException("Get server metadata error, for path: " + serverPath, e);
            }
            ServiceMetadata metadata = GsonUtil.GSON.fromJson(metadataJson, ServiceMetadata.class);
            serviceMetadataList.add(metadata);
        }

        List<EquivalentAddressGroup> servers = serviceMetadataList.stream()
            .map(ServiceMetadata::toAddressGroup)
            .collect(Collectors.toList());
        ResolutionResult result = ResolutionResult.newBuilder()
            .setAddresses(servers)
            .build();
        this.listener.onResult(result);
    }

}
