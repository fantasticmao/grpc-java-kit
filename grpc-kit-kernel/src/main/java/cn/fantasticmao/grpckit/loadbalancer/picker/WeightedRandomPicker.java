package cn.fantasticmao.grpckit.loadbalancer.picker;

import cn.fantasticmao.grpckit.Constant;
import cn.fantasticmao.grpckit.support.AttributeUtil;
import cn.fantasticmao.grpckit.support.ValRef;
import io.grpc.LoadBalancer;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * The weighted random {@link io.grpc.LoadBalancer.SubchannelPicker}, choose a
 * {@link LoadBalancer.Subchannel} for weighted random load balancer.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-24
 */
@ThreadSafe
public class WeightedRandomPicker extends LoadBalancer.SubchannelPicker {
    /**
     * Holds a snapshot of {@link io.grpc.LoadBalancer.Subchannel}es in {@link LoadBalancer}.
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
            filteredList = list.stream()
                .filter(subChannel -> Objects.equals(this.getTag(subChannel), tag))
                .collect(Collectors.toList());
        }
        // if no tag is matched, then return the original subChannels.
        if (filteredList.isEmpty()) {
            filteredList = list;
        }

        // pick a subChannel randomly, taking into account weights of servers.
        final int weightSum = filteredList.stream()
            .mapToInt(this::getWeight)
            .sum();
        final int randomVal = ThreadLocalRandom.current().nextInt(weightSum);
        int randomIndex = filteredList.size() - 1;
        for (int i = 0, sum = 0; i < filteredList.size(); i++) {
            sum += this.getWeight(filteredList.get(i));
            if (randomVal <= sum) {
                randomIndex = i;
                break;
            }
        }
        return LoadBalancer.PickResult.withSubchannel(filteredList.get(randomIndex));
    }

    private String getTag(LoadBalancer.Subchannel subChannel) {
        ValRef<String> tagRef = AttributeUtil.getValRef(subChannel, AttributeUtil.KEY_REF_TAG);
        return tagRef.value;
    }

    private int getWeight(LoadBalancer.Subchannel subChannel) {
        ValRef<Integer> weightRef = AttributeUtil.getValRef(subChannel, AttributeUtil.KEY_REF_WEIGHT);
        return weightRef.value;
    }
}
