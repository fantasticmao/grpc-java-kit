package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.ServiceDiscoveryProvider;
import io.grpc.NameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * A provider for {@link ZkServiceDiscovery}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-07-31
 */
public class ZkServiceDiscoveryProvider extends ServiceDiscoveryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscoveryProvider.class);
    private static final String SCHEME = "zookeeper";

    public ZkServiceDiscoveryProvider() {
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (!SCHEME.equalsIgnoreCase(targetUri.getScheme())) {
            return null;
        }
        String servicePath = targetUri.getPath();
        return new ZkServiceDiscovery(targetUri.getAuthority(), targetUri.getAuthority(), servicePath);
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        try {
            Class.forName("org.apache.curator.framework.CuratorFramework");
            return true;
        } catch (ClassNotFoundException ignored) {
            LOGGER.warn("Unable to load ZooKeeper bases service discovery, missing dependency: curator-framework");
            return false;
        }
    }

    @Override
    protected int priority() {
        // greater than the default value.
        return DEFAULT_PRIORITY + 1;
    }
}
