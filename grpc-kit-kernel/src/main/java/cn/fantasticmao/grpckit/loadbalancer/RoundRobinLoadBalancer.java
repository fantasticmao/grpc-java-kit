package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import io.grpc.Status;

/**
 * The round-robin service load balancer.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-20
 */
class RoundRobinLoadBalancer extends ServiceLoadBalancer {

    @Override
    public void handleNameResolutionError(Status error) {
        // TODO
    }

    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        // TODO
    }

    @Override
    public void shutdown() {
        // TODO
    }
}
