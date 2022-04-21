package cn.fantasticmao.grpckit.loadbalancer.picker;

import io.grpc.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author harrison
 * @since 2022/4/21
 */
public class WeightRobinPicker extends AbstractWeightPicker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeightRobinPicker.class);
    /**
     * Holds a snapshot of {@link io.grpc.LoadBalancer.Subchannel}es in a {@link LoadBalancer}.
     */
    private final List<LoadBalancer.Subchannel> list;

    private volatile int robinIndex;

    private static final AtomicIntegerFieldUpdater<WeightRobinPicker> INDEX_UPDATER =
        AtomicIntegerFieldUpdater.newUpdater(WeightRobinPicker.class, "robinIndex");

    public WeightRobinPicker(List<LoadBalancer.Subchannel> list) {
        this.list = Collections.unmodifiableList(list);
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        List<LoadBalancer.Subchannel> filteredList = filterByTag(this.list, args);
        // if no tag is matched, then the RPC will be buffered in the Channel,
        // until the next picker is provided via Helper.updateBalancingState(),
        // when the RPC will go through the same picking process again.
        if (filteredList.isEmpty()) {
            LOGGER.debug("No tag is matched, the RPC will be buffered in the Channel");
            return LoadBalancer.PickResult.withNoResult();
        }
        LOGGER.debug("SubChannels to be picked: {}", filteredList);
        int size = filteredList.size();
        int i = INDEX_UPDATER.incrementAndGet(this);
        if (i >= size) {
            int oldi = i;
            i %= size;
            INDEX_UPDATER.compareAndSet(this, oldi, i);
        }
        return LoadBalancer.PickResult.withSubchannel(filteredList.get(i));
    }
}
