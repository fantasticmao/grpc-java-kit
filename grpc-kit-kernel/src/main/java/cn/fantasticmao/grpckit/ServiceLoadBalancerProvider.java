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
     * {@inheritDoc}
     */
    @Override
    public abstract boolean isAvailable();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int getPriority();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getPolicyName();
}
