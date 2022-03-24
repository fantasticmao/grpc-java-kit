package cn.fantasticmao.grpckit.loadbalancer.picker;

import io.grpc.LoadBalancer;
import io.grpc.Status;

/**
 * The empty {@link io.grpc.LoadBalancer.SubchannelPicker}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-24
 */
public class EmptyPicker extends LoadBalancer.SubchannelPicker {
    private final Status status;

    public EmptyPicker(Status status) {
        this.status = status;
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        return status.isOk()
            ? LoadBalancer.PickResult.withNoResult()
            : LoadBalancer.PickResult.withError(status);
    }
}
