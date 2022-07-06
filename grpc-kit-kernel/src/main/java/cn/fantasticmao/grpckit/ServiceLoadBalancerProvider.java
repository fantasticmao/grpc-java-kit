package cn.fantasticmao.grpckit;

import io.grpc.LoadBalancerProvider;

/**
 * A provider for {@link ServiceLoadBalancer}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-20
 */
public abstract class ServiceLoadBalancerProvider extends LoadBalancerProvider {
    protected static final int DEFAULT_PRIORITY = 5;

    /**
     * Whether this provider is available for use, taking the current environment into consideration.
     * If {@code false}, {@link #newLoadBalancer} is not safe to be called.
     */
    @Override
    public abstract boolean isAvailable();

    /**
     * A priority, from 0 to 10 that this provider should be used, taking the current environment into
     * consideration. 5 should be considered the default, and then tweaked based on environment
     * detection. A priority of 0 does not imply that the provider wouldn't work; just that it should
     * be last in line.
     */
    @Override
    public abstract int getPriority();

    /**
     * Returns the load-balancing policy name associated with this provider, which makes it selectable
     * via {@link io.grpc.LoadBalancerRegistry#getProvider}.  This is called only when the class is loaded. It
     * shouldn't change, and there is no point doing so.
     *
     * <p>The policy name should consist of only lower case letters letters, underscore and digits,
     * and can only start with letters.
     */
    @Override
    public abstract String getPolicyName();
}
