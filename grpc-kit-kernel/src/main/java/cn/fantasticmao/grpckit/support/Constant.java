package cn.fantasticmao.grpckit.support;

import cn.fantasticmao.grpckit.ServiceMetadata;
import io.grpc.*;

import javax.annotation.Nonnull;

/**
 * Some constants.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-06
 */
public interface Constant {
    String VERSION = "1.39.0";

    CallOptions.Key<String> CALL_OPTION_TAG = CallOptions.Key
        .createWithDefault("tag", ServiceMetadata.DEFAULT_TAG);

    Attributes.Key<Integer> ATTRIBUTE_WEIGHT = Attributes.Key.create("weight");
    Attributes.Key<String> ATTRIBUTE_TAG = Attributes.Key.create("tag");

    /**
     * Keep the {@link ValRef value reference} in a {@link Attributes} of the
     * {@link LoadBalancer.Subchannel}, so that it can be modified.
     */
    Attributes.Key<ValRef<Integer>> ATTRIBUTE_REF_WEIGHT = Attributes.Key.create("weight");
    Attributes.Key<ValRef<String>> ATTRIBUTE_REF_TAG = Attributes.Key.create("tag");
    Attributes.Key<ValRef<ConnectivityStateInfo>> ATTRIBUTE_REF_STATE = Attributes.Key.create("state");

    /**
     * Get the value of a specific key in the {@link Attributes} of a {@link EquivalentAddressGroup}.
     */
    @Nonnull
    static <T> T getAttribute(EquivalentAddressGroup addressGroup, Attributes.Key<T> key,
                              T defaultVal) {
        T attribute = addressGroup.getAttributes().get(key);
        return attribute != null ? attribute : defaultVal;
    }

    /**
     * Get the {@link ValRef} of a specific key in a {@link Attributes} of the {@link LoadBalancer.Subchannel}.
     */
    @Nonnull
    static <T> ValRef<T> getValRef(LoadBalancer.Subchannel subChannel, Attributes.Key<ValRef<T>> key) {
        ValRef<T> ref = subChannel.getAttributes().get(key);
        if (ref == null) {
            String message = String.format("Attribute '%s' in the subChannel '%s' must not be null.", key, subChannel);
            throw new NullPointerException(message);
        }
        return ref;
    }
}
