package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import io.grpc.LoadBalancer;
import io.grpc.Status;

/**
 * The "weighted_random" service load balancer.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-08
 */
class RandomLoadBalancer extends ServiceLoadBalancer {
    private final LoadBalancer.Helper helper;

    public RandomLoadBalancer(Helper helper) {
        this.helper = helper;
    }

    @Override
    public void handleNameResolutionError(Status error) {

    }

    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {

    }

    @Override
    public void shutdown() {

    }
}
