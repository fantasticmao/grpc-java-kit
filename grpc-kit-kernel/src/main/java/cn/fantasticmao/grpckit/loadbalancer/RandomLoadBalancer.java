package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import io.grpc.Status;

/**
 * The "weighted_random" service load balancer.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.LoadBalancer
 * @since 2022-03-08
 */
class RandomLoadBalancer extends ServiceLoadBalancer {

    @Override
    public void handleNameResolutionError(Status error) {

    }

    @Override
    public void shutdown() {

    }
}
