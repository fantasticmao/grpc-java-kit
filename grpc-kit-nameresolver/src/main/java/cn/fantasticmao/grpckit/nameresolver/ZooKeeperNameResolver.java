package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolver;

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

    public ZooKeeperNameResolver() {

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
