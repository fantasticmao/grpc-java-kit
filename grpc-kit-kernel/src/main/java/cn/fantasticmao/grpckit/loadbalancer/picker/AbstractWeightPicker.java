package cn.fantasticmao.grpckit.loadbalancer.picker;

import cn.fantasticmao.grpckit.Constant;
import cn.fantasticmao.grpckit.support.AttributeUtil;
import cn.fantasticmao.grpckit.support.ValRef;
import io.grpc.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author harrison
 * @since 2022/4/21
 */
public abstract class AbstractWeightPicker extends LoadBalancer.SubchannelPicker {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWeightPicker.class);

    /**
     * Make a balancing decision for a new RPC.
     * @param args – the pick arguments
     * @return PickResult
     */
    @Override
    public abstract LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args);

    protected List<LoadBalancer.Subchannel> filterByTag(List<LoadBalancer.Subchannel> filteredList, LoadBalancer.PickSubchannelArgs args) {
        // filter subChannels by the tag in call options.
        final String tag = args.getCallOptions().getOption(Constant.KEY_OPTION_TAG);
        if (tag != null && !tag.isBlank()) {
            LOGGER.debug("Original subChannels: {}", filteredList);
            LOGGER.debug("Tag in call options: {}", tag);
            filteredList = filteredList.stream()
                .filter(subChannel -> Objects.equals(this.getTag(subChannel), tag))
                .collect(Collectors.toList());
        }
        return filteredList;
    }

    private String getTag(LoadBalancer.Subchannel subChannel) {
        ValRef<String> tagRef = AttributeUtil.getValRef(subChannel, AttributeUtil.KEY_REF_TAG);
        return tagRef.value;
    }

}
