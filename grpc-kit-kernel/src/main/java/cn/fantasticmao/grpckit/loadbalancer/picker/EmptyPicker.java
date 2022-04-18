package cn.fantasticmao.grpckit.loadbalancer.picker;

import com.google.common.base.MoreObjects;
import io.grpc.LoadBalancer;
import io.grpc.Status;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

/**
 * The empty {@link io.grpc.LoadBalancer.SubchannelPicker SubchannelPicker}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-24
 */
@ThreadSafe
public class EmptyPicker extends LoadBalancer.SubchannelPicker {
    private final Status status;

    public static final Status EMPTY_OK = Status.OK.withDescription("no subChannels ready");

    public EmptyPicker(Status status) {
        this.status = status;
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        return status.isOk()
            ? LoadBalancer.PickResult.withNoResult()
            : LoadBalancer.PickResult.withError(status);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EmptyPicker)) {
            return false;
        }
        EmptyPicker that = (EmptyPicker) obj;
        return this == that || Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(EmptyPicker.class)
            .add("status", status)
            .toString();
    }
}
