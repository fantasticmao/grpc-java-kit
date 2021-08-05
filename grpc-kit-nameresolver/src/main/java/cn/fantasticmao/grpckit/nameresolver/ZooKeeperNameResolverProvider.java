package cn.fantasticmao.grpckit.nameresolver;

import com.google.common.base.Preconditions;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A provider for {@link ZooKeeperNameResolver}.
 *
 * @author maomao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolverProvider
 * @since 2021-07-31
 */
public class ZooKeeperNameResolverProvider extends NameResolverProvider {
    private static final Logger LOGGER = Logger.getLogger(ZooKeeperNameResolverProvider.class.getName());
    private static final String SCHEME = "zookeeper";

    public ZooKeeperNameResolverProvider() {
    }

    @Override
    protected boolean isAvailable() {
        try {
            Class.forName("org.apache.curator.framework.CuratorFramework");
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.FINE, "Unable to load ZooKeeper NameResolver", e);
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
