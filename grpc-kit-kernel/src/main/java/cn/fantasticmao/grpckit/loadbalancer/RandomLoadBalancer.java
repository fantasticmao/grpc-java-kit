package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceDiscovery;
import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.loadbalancer.picker.FailingPicker;
import cn.fantasticmao.grpckit.loadbalancer.picker.WeightedRandomPicker;
import cn.fantasticmao.grpckit.support.ValRef;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.net.SocketAddress;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.grpc.ConnectivityState.*;

/**
 * The random service load balancer.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-08
 */
class RandomLoadBalancer extends ServiceLoadBalancer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomLoadBalancer.class);

    private final LoadBalancer.Helper helper;

    /**
     * A group of {@link SocketAddress}es that are considered equivalent when channel makes connections.
     *
     * @see EquivalentAddressGroup
     */
    private final Map<List<SocketAddress>, Subchannel> subChannelMap = new HashMap<>();

    public RandomLoadBalancer(Helper helper) {
        this.helper = helper;
    }

    @Override
    public void handleNameResolutionError(Status error) {
        this.helper.updateBalancingState(TRANSIENT_FAILURE, new FailingPicker(error));
    }

    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        List<EquivalentAddressGroup> servers = resolvedAddresses.getAddresses();
        Map<List<SocketAddress>, EquivalentAddressGroup> latestAddressGroupMap = servers.stream()
            .collect(Collectors.toMap(EquivalentAddressGroup::getAddresses, Function.identity()));
        Set<List<SocketAddress>> toRemoveAddressListSet = new HashSet<>(subChannelMap.keySet());
        toRemoveAddressListSet.removeAll(latestAddressGroupMap.keySet());

        for (Map.Entry<List<SocketAddress>, EquivalentAddressGroup> latestEntry : latestAddressGroupMap.entrySet()) {
            final List<SocketAddress> addressList = latestEntry.getKey();
            final EquivalentAddressGroup addressGroup = latestEntry.getValue();
            final Integer weight = RandomLoadBalancer.getAttribute(addressGroup, ServiceDiscovery.KEY_WEIGHT);
            final String tag = RandomLoadBalancer.getAttribute(addressGroup, ServiceDiscovery.KEY_TAG);

            Subchannel existingSubChannel = subChannelMap.get(addressList);
            // update existed subChannel.
            if (existingSubChannel != null) {
                existingSubChannel.updateAddresses(Collections.singletonList(addressGroup));
                RandomLoadBalancer.getValRef(existingSubChannel, KEY_REF_WEIGHT).value = weight;
                RandomLoadBalancer.getValRef(existingSubChannel, KEY_REF_TAG).value = tag;
                continue;
            }
            // create a new subChannel for new addresses.
            Attributes.Builder subChannelAttrs = addressGroup.getAttributes().toBuilder()
                .set(KEY_REF_STATE, new ValRef<>(ConnectivityStateInfo.forNonError(IDLE)))
                .set(KEY_REF_WEIGHT, new ValRef<>(weight))
                .set(KEY_REF_TAG, new ValRef<>(tag));
            final Subchannel subChannel = helper.createSubchannel(CreateSubchannelArgs.newBuilder()
                .setAddresses(addressGroup)
                .setAttributes(subChannelAttrs.build())
                .build());
            subChannel.start(state -> new StateListener(subChannel));
            subChannelMap.put(addressList, subChannel);
            subChannel.requestConnection();
        }

        // remove not existed subChannel.
        List<Subchannel> removedSubChannelList = new ArrayList<>(toRemoveAddressListSet.size());
        for (List<SocketAddress> addressList : toRemoveAddressListSet) {
            removedSubChannelList.add(subChannelMap.remove(addressList));
        }

        // update balancing state.
        this.updateBalancingState();

        // shutdown removed subChannels.
        for (Subchannel subChannel : removedSubChannelList) {
            this.shutdownSubChannel(subChannel);
        }
    }

    @Override
    public void shutdown() {
        for (Subchannel subChannel : subChannelMap.values()) {
            this.shutdownSubChannel(subChannel);
        }
        subChannelMap.clear();
    }

    private void shutdownSubChannel(Subchannel subChannel) {
        subChannel.shutdown();
        ValRef<ConnectivityStateInfo> stateRef = RandomLoadBalancer.getValRef(subChannel, KEY_REF_STATE);
        stateRef.value = ConnectivityStateInfo.forNonError(SHUTDOWN);
    }

    private void updateBalancingState() {
        // filter ready subChannels and check if there is a subChannel in connecting.
        List<Subchannel> readySubChannelList = new LinkedList<>();
        boolean isConnecting = false;
        Status aggStatus = Status.OK.withDescription("no subChannels ready");
        for (Subchannel subChannel : subChannelMap.values()) {
            ValRef<ConnectivityStateInfo> stateRef = RandomLoadBalancer.getValRef(subChannel, KEY_REF_STATE);
            if (stateRef.value.getState() == READY) {
                readySubChannelList.add(subChannel);
            }
            if (stateRef.value.getState() == CONNECTING || stateRef.value.getState() == IDLE) {
                isConnecting = true;
            }
        }

        // update balancing state.
        if (!readySubChannelList.isEmpty()) {
            this.helper.updateBalancingState(READY, new WeightedRandomPicker(readySubChannelList));
        } else {
            // FIXME
            this.helper.updateBalancingState(isConnecting ? CONNECTING : TRANSIENT_FAILURE,
                new WeightedRandomPicker(readySubChannelList));
        }
    }

    /**
     * Receives state changes for one {@link Subchannel}. All methods are run under
     * {@link Helper#getSynchronizationContext}.
     * <p>
     * The {@link ConnectivityState Connectivity State} diagram:
     * <pre>
     *                          +-------------------+
     *                      +---| TRANSIENT_FAILURE |
     *  Try to establish    |   +-------------------+
     *  a connection again  |       ^           ^
     *                      |       |           |  TCP 3-way handshake timing
     *                      v       |           |  out or socket error
     *        +------+    +------------+    +-------+
     * New--->| IDLE |--->| CONNECTING |--->| READY |
     *        +------+    +------------+    +-------+
     *           ^              |               |
     *           |              v               v
     *           +---------------+--------------+
     *                    IDLE TIMEOUT
     * </pre>
     */
    @NotThreadSafe
    final class StateListener implements LoadBalancer.SubchannelStateListener {
        private final Subchannel subChannel;

        StateListener(Subchannel subChannel) {
            this.subChannel = subChannel;
        }

        @Override
        public void onSubchannelState(ConnectivityStateInfo newState) {
            List<SocketAddress> addressList = subChannel.getAddresses().getAddresses();
            if (RandomLoadBalancer.this.subChannelMap.get(addressList) != subChannel) {
                LOGGER.warn("The two subChannel instances are not the same when state changes.");
                return;
            }
            if (newState.getState() == TRANSIENT_FAILURE || newState.getState() == IDLE) {
                RandomLoadBalancer.this.helper.refreshNameResolution();
            }
            if (newState.getState() == IDLE) {
                subChannel.requestConnection();
            }

            ValRef<ConnectivityStateInfo> currentStateRef = RandomLoadBalancer.getValRef(subChannel, KEY_REF_STATE);
            if (currentStateRef.value.getState() == TRANSIENT_FAILURE) {
                if (newState.getState() == CONNECTING || newState.getState() == IDLE) {
                    return;
                }
            }
            currentStateRef.value = newState;
            RandomLoadBalancer.this.updateBalancingState();
        }
    }
}
