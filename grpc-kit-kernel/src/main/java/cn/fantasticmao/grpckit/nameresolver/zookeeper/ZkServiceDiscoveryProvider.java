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
public class ZkServiceDiscoveryProvider extends ServiceDiscoveryProvider implements ZkServiceBased {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscoveryProvider.class);

    public ZkServiceDiscoveryProvider() {
    }

    @Override
    protected boolean isAvailable() {
        try {
            Class.forName("org.apache.curator.framework.CuratorFramework");
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unable to load ZooKeeper NameResolver, can't found dependency: curator-framework", e);
            return false;
        }
    }

    @Override
    protected int priority() {
        // greater than the default value
        return DEFAULT_PRIORITY + 1;
    }

    @Override
    public NameResolver newNameResolver(URI serviceUri, NameResolver.Args args) {
        if (!SCHEME.equalsIgnoreCase(serviceUri.getScheme())) {
            return null;
        }
        return new ZkServiceDiscovery(serviceUri, args);
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }
}
