package cn.fantasticmao.grpckit.loadbalancer.picker;

import io.grpc.LoadBalancer;
import io.grpc.Status;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The failing {@link io.grpc.LoadBalancer.SubchannelPicker}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-24
 */
@ThreadSafe
public class FailingPicker extends LoadBalancer.SubchannelPicker {
    private final Status failure;

    public FailingPicker(Status failure) {
        this.failure = failure;
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        return LoadBalancer.PickResult.withError(failure);
    }
}
