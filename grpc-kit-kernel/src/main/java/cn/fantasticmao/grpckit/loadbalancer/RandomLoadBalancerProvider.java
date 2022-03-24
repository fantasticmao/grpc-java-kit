package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.ServiceLoadBalancerProvider;
import io.grpc.LoadBalancer;

/**
 * A provider for {@link RandomLoadBalancer}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-20
 */
public class RandomLoadBalancerProvider extends ServiceLoadBalancerProvider {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getPriority() {
        // greater than the default value.
        return DEFAULT_PRIORITY + 1;
    }

    @Override
    public String getPolicyName() {
        return ServiceLoadBalancer.Policy.WEIGHTED_RANDOM.name;
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new RandomLoadBalancer(helper);
    }
}
