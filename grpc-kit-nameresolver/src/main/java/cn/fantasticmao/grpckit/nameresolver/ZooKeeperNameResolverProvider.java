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
    public static final String VM_OPTION = "io.grpc.NameResolverProvider.switch.zookeeper";

    private static final Logger LOGGER = Logger.getLogger(ZooKeeperNameResolverProvider.class.getName());
    private static final String SCHEME = "zookeeper";

    public ZooKeeperNameResolverProvider() {
    }

    @Override
    protected String getVmOptionKey() {
        return VM_OPTION;
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
            LOGGER.log(Level.FINE, "Unable to find ZooKeeper NameResolver", e);
            return false;
        }
    }

    @Override
    public ZooKeeperNameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        try {
            return new ZooKeeperNameResolver();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Connect to ZooKeeper error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }
}
