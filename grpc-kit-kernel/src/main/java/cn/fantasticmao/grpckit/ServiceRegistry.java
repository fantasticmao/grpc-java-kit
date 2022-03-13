package cn.fantasticmao.grpckit;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * Register service instance, the implementation is independent of gRPC.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public abstract class ServiceRegistry implements Closeable {

    /**
     * Register service instance.
     *
     * @param serviceName service name
     * @param address     service address
     * @return if succeed
     */
    public abstract boolean doRegister(String serviceName, InetSocketAddress address);

    @Override
    public abstract void close();

    /**
     * A provider for {@link ServiceRegistry}.
     *
     * @author fantasticmao
     * @version 1.39.0
     * @since 2022-03-13
     */
    public abstract static class Provider implements Comparable<Provider> {
        protected static final int DEFAULT_PRIORITY = 5;

        public abstract ServiceRegistry newServiceRegistry(URI serviceUri);

        public abstract String getDefaultScheme();

        public abstract boolean isAvailable();

        public abstract int priority();

        @Override
        public int compareTo(Provider that) {
            int i = this.priority() - that.priority();
            if (i != 0) {
                return i;
            } else {
                return this.getClass().getSimpleName()
                    .compareTo(that.getClass().getSimpleName());
            }
        }
    }
}
