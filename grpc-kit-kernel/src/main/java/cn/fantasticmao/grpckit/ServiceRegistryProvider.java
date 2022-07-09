package cn.fantasticmao.grpckit;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;

/**
 * A provider for {@link ServiceRegistry}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public abstract class ServiceRegistryProvider implements Comparable<ServiceRegistryProvider> {
    protected static final int DEFAULT_PRIORITY = 5;

    /**
     * Creates a {@link ServiceRegistry} for the given service URI.
     *
     * @param serviceUri the service URI to be registered, whose scheme must not be {@code null}
     */
    @Nullable
    public abstract ServiceRegistry newServiceRegistry(ServiceURI serviceUri, InetSocketAddress address);

    public abstract boolean isAvailable();

    public abstract int priority();

    @Override
    public int compareTo(ServiceRegistryProvider that) {
        int i = that.priority() - this.priority();
        if (i != 0) {
            return i;
        } else {
            return that.getClass().getSimpleName()
                .compareTo(this.getClass().getSimpleName());
        }
    }
}
