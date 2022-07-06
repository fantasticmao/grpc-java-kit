package cn.fantasticmao.grpckit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.URI;

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
     * @param targetUri the target URI to be resolved, whose scheme must not be {@code null}
     */
    @Nullable
    public abstract ServiceRegistry newServiceRegistry(URI targetUri, @Nonnull InetSocketAddress address);

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
