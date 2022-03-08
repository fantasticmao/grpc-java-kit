package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import com.google.common.base.Preconditions;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * A provider for {@link ZooKeeperNameResolver}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolverProvider
 * @since 2021-07-31
 */
public class ZooKeeperNameResolverProvider extends NameResolverProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperNameResolverProvider.class);
    private static final String SCHEME = "zookeeper";

    public ZooKeeperNameResolverProvider() {
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
    public ZooKeeperNameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (SCHEME.equals(targetUri.getScheme())) {
            String targetPath = Preconditions.checkNotNull(targetUri.getPath(), "targetPath");
            if (!targetPath.startsWith("/")) {
                targetPath = "/" + targetPath;
            }
            return new ZooKeeperNameResolver(targetPath);
        } else {
            return null;
        }
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }
}
