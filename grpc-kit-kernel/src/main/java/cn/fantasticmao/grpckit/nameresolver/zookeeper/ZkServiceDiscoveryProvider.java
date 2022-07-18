package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.ServiceDiscoveryProvider;
import cn.fantasticmao.grpckit.ServiceURI;
import io.grpc.NameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
    public NameResolver newNameResolver(ServiceURI serviceUri, NameResolver.Args args) {
        if (!SCHEME.equals(serviceUri.registryUri.getScheme())) {
            return null;
        }
        String authority = serviceUri.registryUri.getAuthority();
        String connectString = serviceUri.registryUri.getAuthority();
        String servicePath = String.format("/%s/%s/servers", serviceUri.appName,
            Objects.requireNonNull(serviceUri.appGroup, "serviceUri.appGroup must not be null"));
        return new ZkServiceDiscovery(authority, connectString, servicePath);
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
            LOGGER.warn("Unsupported ZkServiceDiscoveryProvider, missing dependency: curator-framework");
            return false;
        }
    }

    @Override
    protected int priority() {
        // greater than the default value.
        return DEFAULT_PRIORITY + 1;
    }
}
