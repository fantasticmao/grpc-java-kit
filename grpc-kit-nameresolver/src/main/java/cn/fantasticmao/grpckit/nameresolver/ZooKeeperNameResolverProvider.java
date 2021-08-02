package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolver;

import java.io.IOException;
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
public class ZooKeeperNameResolverProvider extends AbstractNameResolverProvider {
    public static final String VM_SWITCH = "io.grpc.NameResolverProvider.switch.zookeeper";

    private static final Logger logger = Logger.getLogger(ZooKeeperNameResolverProvider.class.getName());
    private static final String SCHEME = "zookeeper";

    public ZooKeeperNameResolverProvider() {
    }

    @Override
    protected String getVmSwitchKey() {
        return VM_SWITCH;
    }

    @Override
    protected boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }

        try {
            Class.forName("org.apache.zookeeper.ZooKeeper");
            return true;
        } catch (ClassNotFoundException e) {
            logger.log(Level.FINE, "Unable to find ZooKeeper NameResolver", e);
            return false;
        }
    }

    @Override
    public ZooKeeperNameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        // TODO how to pass args from ManagedChannel to there?
        try {
            return new ZooKeeperNameResolver(new String[]{"localhost:2181"}, 10_000);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "connect to zookeeper error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }
}
