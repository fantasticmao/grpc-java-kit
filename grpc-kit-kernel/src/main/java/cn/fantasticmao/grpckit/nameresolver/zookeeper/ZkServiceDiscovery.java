package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.Constant;
import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceDiscovery;
import cn.fantasticmao.grpckit.ServiceMetadata;
import io.grpc.NameResolver;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A ZooKeeper based {@link ServiceDiscovery}.
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
 * @since 2021-07-31
 */
class ZkServiceDiscovery extends ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);

    private final String connectString;
    private final String servicePath;
    private final CuratorFramework zkClient;

    ZkServiceDiscovery(URI serviceUri, NameResolver.Args args) {
        super(args.getOffloadExecutor());

        this.connectString = serviceUri.getAuthority();
        this.servicePath = serviceUri.getPath();
        this.zkClient = ZkClientHolder.get(this.connectString);

        try {
            this.zkClient.blockUntilConnected(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new GrpcKitException("Connect to ZooKeeper error, connect string: " + this.connectString, e);
        }
    }

    @Override
    public String getServiceAuthority() {
        return this.connectString;
    }

    @Override
    public List<ServiceMetadata> lookup() {
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
            ServiceMetadata metadata = Constant.GSON.fromJson(metadataJson, ServiceMetadata.class);
            serviceMetadataList.add(metadata);
        }
        return serviceMetadataList;
    }

    @Override
    public void shutdown() {
        LOGGER.warn("Shutdown {}", this.getClass().getName());
    }
}
