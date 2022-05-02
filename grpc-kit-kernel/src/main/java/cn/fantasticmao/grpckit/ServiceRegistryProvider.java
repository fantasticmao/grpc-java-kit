package cn.fantasticmao.grpckit;

import javax.annotation.Nullable;
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
     * <p>
     * Example URIs:
     * <ul>
     *     <li>zookeeper://localhost:2181/app_name/default/servers/192.168.1.1:8080</li>
     *     <li>consul://localhost:8500/app_name/default/servers/192.168.1.1:8080</li>
     * </ul>
     *
     * @param serviceUri the service URI to be resolved.
     */
    @Nullable
    public abstract ServiceRegistry newServiceRegistry(URI serviceUri);

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
