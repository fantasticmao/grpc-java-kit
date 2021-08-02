package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolver;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
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
    private static final Logger logger = Logger.getLogger(ZooKeeperNameResolver.class.getName());

    private final ZooKeeper zooKeeper;

    public ZooKeeperNameResolver(String[] addresses, int sessionTimeout) throws IOException {
        String connectString = String.join(",", addresses);
        this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, event -> {
            if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())
                && Watcher.Event.EventType.None.equals(event.getType())) {
                logger.info("watch zookeeper connected ...");
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
