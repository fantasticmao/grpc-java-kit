package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.Status;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The "weighted_random" service load balancer.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-08
 */
class RandomLoadBalancer extends ServiceLoadBalancer {
    private final LoadBalancer.Helper helper;
    private final Map<EquivalentAddressGroup, Subchannel> subChannelMap = new HashMap<>();

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

    static class RandomPicker extends SubchannelPicker {
        private final Random random;

        public RandomPicker() {
            this.random = new Random();
        }

        @Override
        public PickResult pickSubchannel(PickSubchannelArgs args) {
            return PickResult.withSubchannel(null);
        }
    }
}
