package cn.fantasticmao.grpckit;

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

    public abstract ServiceRegistry newServiceRegistry(URI serviceUri);

    public abstract String getDefaultScheme();

    public abstract boolean isAvailable();

    public abstract int priority();

    @Override
    public int compareTo(ServiceRegistryProvider that) {
        int i = this.priority() - that.priority();
        if (i != 0) {
            return i;
        } else {
            return this.getClass().getSimpleName()
                .compareTo(that.getClass().getSimpleName());
        }
    }
}
