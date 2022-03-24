package cn.fantasticmao.grpckit;

import cn.fantasticmao.grpckit.support.ValRef;
import io.grpc.*;

import javax.annotation.Nonnull;

/**
 * Service load balancer, implemented by using gRPC {@link io.grpc.LoadBalancer}
 * and {@link io.grpc.LoadBalancerProvider} plugins.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.AutoConfiguredLoadBalancerFactory.AutoConfiguredLoadBalancer
 * @since 2022-03-20
 */
public abstract class ServiceLoadBalancer extends LoadBalancer {
    /**
     * Keep value references in {@link Attributes} of {@link LoadBalancer.Subchannel}, so that it can be modified.
     */
    public static final Attributes.Key<ValRef<ConnectivityStateInfo>> KEY_REF_STATE = Attributes.Key.create("state");
    public static final Attributes.Key<ValRef<Integer>> KEY_REF_WEIGHT = Attributes.Key.create("weight");
    public static final Attributes.Key<ValRef<String>> KEY_REF_TAG = Attributes.Key.create("tag");

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

    /**
     * Get the value of a specified key in {@link Attributes} of {@link EquivalentAddressGroup}.
     */
    @Nonnull
    public static <T> T getAttribute(EquivalentAddressGroup addressGroup, Attributes.Key<T> key) {
        T attribute = addressGroup.getAttributes().get(key);
        if (attribute == null) {
            String message = String.format("Attribute '%s' in addressGroup can not be null.", key);
            throw new NullPointerException(message);
        }
        return attribute;
    }

    /**
     * Get the {@link ValRef} of a specified key in {@link Attributes} of {@link LoadBalancer.Subchannel}.
     */
    @Nonnull
    public static <T> ValRef<T> getValRef(Subchannel subChannel, Attributes.Key<ValRef<T>> key) {
        ValRef<T> ref = subChannel.getAttributes().get(key);
        if (ref == null) {
            String message = String.format("Attribute '%s' in subChannel can not be null.", key);
            throw new NullPointerException(message);
        }
        return ref;
    }

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
