package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * A provider for {@link ZkNameResolver}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolverProvider
 * @since 2021-07-31
 */
public class ZkNameResolverProvider extends NameResolverProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkNameResolverProvider.class);
    private static final String SCHEME = "zookeeper";

    public ZkNameResolverProvider() {
    }

    @Override
    protected boolean isAvailable() {
        try {
            Class.forName("org.apache.curator.framework.CuratorFramework");
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unable to load ZooKeeper NameResolver", e);
            return false;
        }
    }

    @Override
    protected int priority() {
        // less than the default value
        return 5 - 1;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (!SCHEME.equalsIgnoreCase(targetUri.getScheme())) {
            return null;
        }

        // TODO check arguments
        String connectString = targetUri.getAuthority();
        int sessionTimeout = 5_000;
        String serviceName = targetUri.getPath();
        return new ZkNameResolver(connectString, sessionTimeout, serviceName);
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }
}
