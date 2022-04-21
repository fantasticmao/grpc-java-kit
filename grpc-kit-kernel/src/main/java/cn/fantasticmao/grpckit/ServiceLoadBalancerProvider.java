package cn.fantasticmao.grpckit;

import io.grpc.LoadBalancerProvider;
import io.grpc.LoadBalancerRegistry;

/**
 * A provider for {@link ServiceLoadBalancer}.
 *
 * 每个provider都绑定到一个load-balancing policy。
 * gRPC可以通过Java的SPI机制自动发现service的实现，
 * 对于自动发现，service的实现必须有一个无参数的构造函数，
 * 并jar包含一个名为META-INF/services/io.grpc.LoadBalancerProvider的文件，文件的内容应该是service实现的类名。
 * 在构造函数中需要参数的实现可以通过${@link LoadBalancerRegistry#register}手动注册。
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-20
 */
public abstract class ServiceLoadBalancerProvider extends LoadBalancerProvider {
    protected static final int DEFAULT_PRIORITY = 5;

    /**
     * Implementations should not throw. If they do, it may interrupt class loading.
     * If exceptions may reasonably occur for implementation-specific reasons,
     * implementations should generally handle the exception gracefully
     * and return false from isAvailable().
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
