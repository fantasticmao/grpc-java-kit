package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceLoadBalancerProvider;
import io.grpc.LoadBalancer;

/**
 * A provider for {@link RoundRobinLoadBalancer}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-20
 */
public class RoundRobinLoadBalancerProvider extends ServiceLoadBalancerProvider {

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public int getPriority() {
        // greater than the default value
        return DEFAULT_PRIORITY + 1;
    }

    @Override
    public String getPolicyName() {
        return "weighted_round_robin";
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return null;
    }
}
