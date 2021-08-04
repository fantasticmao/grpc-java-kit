package cn.fantasticmao.grpckit.nameresolver;

import cn.fantasticmao.grpckit.common.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.common.constant.Constant;
import io.grpc.NameResolver;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A ZooKeeper-based {@link NameResolver}.
 *
 * @author maomao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolver
 * @see <a href="https://zookeeper.apache.org/">Apache ZooKeeper</a>
 * @since 2021-07-31
 */
public class ZooKeeperNameResolver extends NameResolver {
    private static final Logger LOGGER = Logger.getLogger(ZooKeeperNameResolver.class.getName());

    private final ZooKeeper zooKeeper;

    public ZooKeeperNameResolver() throws IOException {
        String connectString = GrpcKitConfig.getInstance().getValue(Constant.ConfigKey.ZOOKEEPER_CONNECT_STRING);
        Objects.requireNonNull(connectString, "ZooKeeper connect string can not be null");

        int sessionTimeout;
        int defaultSessionTimeout = 5_000;
        String sessionTimeoutStr = GrpcKitConfig.getInstance().getValue(Constant.ConfigKey.ZOOKEEPER_SESSION_TIMEOUT,
            String.valueOf(defaultSessionTimeout));
        try {
            sessionTimeout = Integer.parseInt(sessionTimeoutStr);
        } catch (NumberFormatException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "ZooKeeper session timeout number format error, fallback to " +
                    defaultSessionTimeout + " ms", e);
            }
            sessionTimeout = defaultSessionTimeout;
        }

        this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, event -> {
            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())
                && Watcher.Event.EventType.None.equals(event.getType())) {
                LOGGER.info("Watch zookeeper connected ...");
            }
        });
    }

    @Override
    public String getServiceAuthority() {
        return null;
    }

    @Override
    public void start(Listener2 listener) {
        super.start(listener);
    }

    @Override
    public void shutdown() {

    }
}
