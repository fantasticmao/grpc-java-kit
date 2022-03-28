package cn.fantasticmao.grpckit.support;

import io.grpc.Attributes;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;

import javax.annotation.Nonnull;

/**
 * A util class for the {@link Attributes gRPC Attributes}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-27
 */
public interface AttributeUtil {
    Attributes.Key<Integer> KEY_WEIGHT = Attributes.Key.create("weight");
    Attributes.Key<String> KEY_TAG = Attributes.Key.create("tag");

    /**
     * Get the value of a specific key in the {@link Attributes} of a {@link EquivalentAddressGroup}.
     */
    @Nonnull
    static <T> T getAttribute(EquivalentAddressGroup addressGroup, Attributes.Key<T> key) {
        T attribute = addressGroup.getAttributes().get(key);
        if (attribute == null) {
            String message = String.format("Attribute '%s' in addressGroup can not be null.", key);
            throw new NullPointerException(message);
        }
        return attribute;
    }

    /**
     * Keep the {@link ValRef value reference} in a {@link Attributes} of the
     * {@link LoadBalancer.Subchannel}, so that it can be modified.
     */
    Attributes.Key<ValRef<Integer>> KEY_REF_WEIGHT = Attributes.Key.create("weight");
    Attributes.Key<ValRef<String>> KEY_REF_TAG = Attributes.Key.create("tag");
    Attributes.Key<ValRef<ConnectivityStateInfo>> KEY_REF_STATE = Attributes.Key.create("state");

    /**
     * Get the {@link ValRef} of a specific key in a {@link Attributes} of the {@link LoadBalancer.Subchannel}.
     */
    @Nonnull
    static <T> ValRef<T> getValRef(LoadBalancer.Subchannel subChannel, Attributes.Key<ValRef<T>> key) {
        ValRef<T> ref = subChannel.getAttributes().get(key);
        if (ref == null) {
            String message = String.format("Attribute '%s' in subChannel can not be null.", key);
            throw new NullPointerException(message);
        }
        return ref;
    }
}
