package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.ServiceRegistry;
import cn.fantasticmao.grpckit.ServiceRegistryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * A provider for {@link ZkServiceRegistry}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public class ZkServiceRegistryProvider extends ServiceRegistryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistryProvider.class);
    private static final String SCHEME = "zookeeper";

    public ZkServiceRegistryProvider() {
    }

    @Nullable
    @Override
    public ServiceRegistry newServiceRegistry(URI targetUri, @Nonnull InetSocketAddress address) {
        if (!SCHEME.equalsIgnoreCase(targetUri.getScheme())) {
            return null;
        }
        // FIXME
        String servicePath = targetUri.getPath() + String.format("/%s:%d",
            address.getAddress().getHostAddress(), address.getPort());
        return new ZkServiceRegistry(targetUri.getAuthority(), servicePath);
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("org.apache.curator.framework.CuratorFramework");
            return true;
        } catch (ClassNotFoundException ignored) {
            LOGGER.warn("Unable to load ZooKeeper bases service registry, missing dependency: curator-framework");
            return false;
        }
    }

    @Override
    public int priority() {
        // greater than the default value.
        return DEFAULT_PRIORITY + 1;
    }
}
