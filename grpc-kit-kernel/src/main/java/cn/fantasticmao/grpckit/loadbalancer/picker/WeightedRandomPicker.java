package cn.fantasticmao.grpckit.loadbalancer.picker;

import cn.fantasticmao.grpckit.Constant;
import cn.fantasticmao.grpckit.support.AttributeUtil;
import cn.fantasticmao.grpckit.support.ValRef;
import com.google.common.base.MoreObjects;
import io.grpc.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * The weighted random {@link io.grpc.LoadBalancer.SubchannelPicker}, choose a
 * {@link LoadBalancer.Subchannel} randomly for a load balancer, and taking into
 * account weights of servers.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-24
 */
@ThreadSafe
public class WeightedRandomPicker extends LoadBalancer.SubchannelPicker {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeightedRandomPicker.class);
    /**
     * Holds a snapshot of {@link io.grpc.LoadBalancer.Subchannel}es in a {@link LoadBalancer}.
     */
    private final List<LoadBalancer.Subchannel> list;

    public WeightedRandomPicker(List<LoadBalancer.Subchannel> list) {
        this.list = Collections.unmodifiableList(list);
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        List<LoadBalancer.Subchannel> filteredList = list;
        // filter subChannels by the tag in call options.
        final String tag = args.getCallOptions().getOption(Constant.KEY_OPTION_TAG);
        if (tag != null && !tag.isBlank()) {
            LOGGER.debug("Original subChannels: {}", filteredList);
            LOGGER.debug("Tag in call options: {}", tag);
            filteredList = list.stream()
                .filter(subChannel -> Objects.equals(this.getTag(subChannel), tag))
                .collect(Collectors.toList());
        }
        // if no tag is matched, then return the original subChannels.
        if (filteredList.isEmpty()) {
            filteredList = list;
        }
        LOGGER.debug("SubChannels to be picked: {}", filteredList);

        // pick a subChannel randomly, taking into account weights of servers.
        final int weightSum = filteredList.stream()
            .mapToInt(this::getWeight)
            .sum();
        final int randomVal = ThreadLocalRandom.current().nextInt(weightSum);
        LOGGER.debug("Next random value: {}", randomVal);

        int randomIndex = filteredList.size() - 1;
        for (int i = 0, sum = 0; i < filteredList.size(); i++) {
            sum += this.getWeight(filteredList.get(i));
            if (randomVal <= sum) {
                randomIndex = i;
                break;
            }
        }

        LoadBalancer.Subchannel pickedSubChannel = filteredList.get(randomIndex);
        LOGGER.debug("SubChannel picked: {}", pickedSubChannel);
        return LoadBalancer.PickResult.withSubchannel(pickedSubChannel);
    }

    private String getTag(LoadBalancer.Subchannel subChannel) {
        ValRef<String> tagRef = AttributeUtil.getValRef(subChannel, AttributeUtil.KEY_REF_TAG);
        return tagRef.value;
    }

    private int getWeight(LoadBalancer.Subchannel subChannel) {
        ValRef<Integer> weightRef = AttributeUtil.getValRef(subChannel, AttributeUtil.KEY_REF_WEIGHT);
        return weightRef.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WeightedRandomPicker)) {
            return false;
        }
        WeightedRandomPicker that = (WeightedRandomPicker) obj;
        return this == that || Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(WeightedRandomPicker.class)
            .add("list", list)
            .toString();
    }
}
