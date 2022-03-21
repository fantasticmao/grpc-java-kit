package cn.fantasticmao.grpckit;

import io.grpc.LoadBalancer;
import io.grpc.Status;

/**
 * Service load balancer, implemented by using gRPC {@link io.grpc.LoadBalancer}
 * and {@link io.grpc.LoadBalancerProvider} plugins.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-20
 */
public abstract class ServiceLoadBalancer extends LoadBalancer {

    /**
     * {@inheritDoc}
     */
    public abstract void handleResolvedAddresses(ResolvedAddresses resolvedAddresses);

    /**
     * {@inheritDoc}
     */
    public abstract void handleNameResolutionError(Status error);

    /**
     * {@inheritDoc}
     */
    public abstract void shutdown();

    public enum Policy {
        /**
         * The pick-first balancing policy.
         *
         * @see io.grpc.internal.PickFirstLoadBalancerProvider
         */
        PICK_FIRST("pick_first"),

        /**
         * The round-robin balancing policy.
         *
         * @see io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider
         */
        ROUND_ROBIN("round_robin"),

        /**
         * The weighted round-robin balancing policy (the default policy).
         *
         * @see cn.fantasticmao.grpckit.loadbalancer.RoundRobinLoadBalancerProvider
         */
        WEIGHTED_ROUND_ROBIN("weighted_round_robin"),

        /**
         * The weighted random balancing policy.
         *
         * @see cn.fantasticmao.grpckit.loadbalancer.RandomLoadBalancerProvider
         */
        WEIGHTED_RANDOM("weighted_random"),

        /**
         * The weighted least-number-of-active-connections balancing policy.
         */
        WEIGHTED_LEAST_CONN("weighted_least_conn"),

        /**
         * The weighted least-average-response-time balancing policy.
         */
        WEIGHTED_LEAST_TIME("weighted_least_time");

        public final String name;

        Policy(String name) {
            this.name = name;
        }
    }
}
