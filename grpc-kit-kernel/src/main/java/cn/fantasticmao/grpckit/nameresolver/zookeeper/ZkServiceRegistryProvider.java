package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.ServiceRegistry;
import cn.fantasticmao.grpckit.ServiceRegistryProvider;
import cn.fantasticmao.grpckit.ServiceURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Objects;

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
    public ServiceRegistry newServiceRegistry(ServiceURI serviceUri, InetSocketAddress address) {
        if (!SCHEME.equalsIgnoreCase(serviceUri.registryUri.getScheme())) {
            return null;
        }
        String connectString = serviceUri.registryUri.getAuthority();
        String servicePath = String.format("/%s/%s/servers/%s:%d", serviceUri.appName,
            Objects.requireNonNull(serviceUri.appGroup, "serviceUri.appGroup must not be null"),
            address.getAddress().getHostAddress(), address.getPort());
        return new ZkServiceRegistry(connectString, servicePath);
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
