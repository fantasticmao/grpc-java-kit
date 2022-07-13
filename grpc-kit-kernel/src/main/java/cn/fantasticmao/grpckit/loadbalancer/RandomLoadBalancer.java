package cn.fantasticmao.grpckit.loadbalancer;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.loadbalancer.picker.EmptyPicker;
import cn.fantasticmao.grpckit.loadbalancer.picker.WeightedRandomPicker;
import cn.fantasticmao.grpckit.support.Constant;
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
 * Service load balancer for the {@link ServiceLoadBalancer.Policy#WEIGHTED_RANDOM weighted random} policy.
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

    /**
     * The current {@link ConnectivityState} in {@link LoadBalancer}.
     */
    private ConnectivityState currentState;
    /**
     * The current {@link LoadBalancer.SubchannelPicker} in {@link LoadBalancer}..
     */
    private LoadBalancer.SubchannelPicker currentPicker;

    public RandomLoadBalancer(Helper helper) {
        this.helper = helper;
    }

    @Override
    public void handleNameResolutionError(Status error) {
        if (this.currentState != READY) {
            updateBalancingState(TRANSIENT_FAILURE, new EmptyPicker(error));
        }
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
            final Integer weight = Constant.getAttribute(addressGroup, Constant.ATTRIBUTE_WEIGHT,
                ServiceMetadata.DEFAULT_WEIGHT);
            final String tag = Constant.getAttribute(addressGroup, Constant.ATTRIBUTE_TAG,
                ServiceMetadata.DEFAULT_TAG);

            Subchannel existingSubChannel = subChannelMap.get(addressList);
            // update existed subChannel.
            if (existingSubChannel != null) {
                existingSubChannel.updateAddresses(Collections.singletonList(addressGroup));
                Constant.getValRef(existingSubChannel, Constant.ATTRIBUTE_REF_WEIGHT).value = weight;
                Constant.getValRef(existingSubChannel, Constant.ATTRIBUTE_REF_TAG).value = tag;
                continue;
            }
            // create a new subChannel for new addresses.
            final Subchannel subChannel = helper.createSubchannel(CreateSubchannelArgs.newBuilder()
                .setAddresses(new EquivalentAddressGroup(addressList))
                .setAttributes(Attributes.newBuilder()
                    .set(Constant.ATTRIBUTE_REF_STATE, new ValRef<>(ConnectivityStateInfo.forNonError(IDLE)))
                    .set(Constant.ATTRIBUTE_REF_WEIGHT, new ValRef<>(weight))
                    .set(Constant.ATTRIBUTE_REF_TAG, new ValRef<>(tag))
                    .build())
                .build());
            subChannel.start(new StateListener(subChannel));
            subChannelMap.put(addressList, subChannel);
            LOGGER.debug("Create SubChannel by address group: {} and attributes: {}", subChannel.getAddresses(),
                subChannel.getAttributes());

            // subChannel state will be changed from IDLE to CONNECTING.
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
        ValRef<ConnectivityStateInfo> stateRef = Constant.getValRef(subChannel, Constant.ATTRIBUTE_REF_STATE);
        stateRef.value = ConnectivityStateInfo.forNonError(SHUTDOWN);
    }

    private void updateBalancingState() {
        // filter ready subChannels and check if there is a subChannel in connecting.
        List<Subchannel> readySubChannelList = new ArrayList<>(subChannelMap.values().size());
        boolean isConnecting = false;
        Status aggStatus = EmptyPicker.EMPTY_OK;
        for (Subchannel subChannel : subChannelMap.values()) {
            ValRef<ConnectivityStateInfo> stateRef = Constant.getValRef(subChannel, Constant.ATTRIBUTE_REF_STATE);
            if (stateRef.value.getState() == READY) {
                readySubChannelList.add(subChannel);
            }
            if (stateRef.value.getState() == CONNECTING || stateRef.value.getState() == IDLE) {
                isConnecting = true;
            }
            // try to find an available status
            if (aggStatus == EmptyPicker.EMPTY_OK || !aggStatus.isOk()) {
                aggStatus = stateRef.value.getStatus();
            }
        }

        // update balancing state.
        if (!readySubChannelList.isEmpty()) {
            updateBalancingState(READY, new WeightedRandomPicker(readySubChannelList));
        } else {
            updateBalancingState(isConnecting ? CONNECTING : TRANSIENT_FAILURE,
                new EmptyPicker(aggStatus));
        }
    }

    private void updateBalancingState(ConnectivityState state, LoadBalancer.SubchannelPicker picker) {
        if (this.currentState != state || this.currentPicker != picker) {
            this.helper.updateBalancingState(state, picker);
            LOGGER.debug("Update balancing by newState: {} and newPicker: {}", state, picker);
            currentState = state;
            currentPicker = picker;
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
     *           +--------------+---------------+
     *                    IDLE TIMEOUT
     * </pre>
     * <p>
     * The following list lists the legal transitions from one state to another and corresponding reasons:
     * <ul>
     *     <li><b>IDLE</b> to <b>CONNECTING</b>: Any new RPC activity on the channel</li>
     *     <li><b>CONNECTING</b> to <b>CONNECTING</b>: Incremental progress during connection establishment</li>
     *     <li><b>CONNECTING</b> to <b>READY</b>: All steps needed to establish a connection succeeded</li>
     *     <li><b>CONNECTING</b> to <b>TRANSIENT_FAILURE</b>: Any failure in any of the steps needed to establish connection</li>
     *     <li><b>CONNECTING</b> to <b>IDLE</b>: No RPC activity on channel for IDLE_TIMEOUT</li>
     *     <li><b>READY</b> to <b>READY</b>: Incremental successful communication on established channel.</li>
     *     <li><b>READY</b> to <b>TRANSIENT_FAILURE</b>: Any failure encountered while expecting successful communication on established channel</li>
     *     <li><b>READY</b> to <b>IDLE</b>: No RPC activity on channel for IDLE_TIMEOUT OR upon receiving a GOAWAY while there are no pending RPCs</li>
     *     <li><b>TRANSIENT_FAILURE</b> to <b>CONNECTING</b>: Wait time required to implement (exponential) backoff is over</li>
     * </ul>
     *
     * @see ConnectivityState
     * @see <a href="https://github.com/grpc/grpc/blob/master/doc/connectivity-semantics-and-api.md">gRPC Connectivity Semantics and API</a>
     */
    @NotThreadSafe
    final class StateListener implements LoadBalancer.SubchannelStateListener {
        private final Subchannel subChannel;

        StateListener(Subchannel subChannel) {
            this.subChannel = subChannel;
        }

        @Override
        public void onSubchannelState(ConnectivityStateInfo newState) {
            LOGGER.debug("Listening state changes in SubChannel (address group: {}), new state: {}",
                subChannel.getAddresses(), newState);

            List<SocketAddress> addressList = subChannel.getAddresses().getAddresses();
            if (RandomLoadBalancer.this.subChannelMap.get(addressList) != subChannel) {
                LOGGER.warn("The two subChannels are not the same when state changes.");
                return;
            }
            if (newState.getState() == TRANSIENT_FAILURE || newState.getState() == IDLE) {
                RandomLoadBalancer.this.helper.refreshNameResolution();
            }
            if (newState.getState() == IDLE) {
                subChannel.requestConnection();
            }

            ValRef<ConnectivityStateInfo> currentStateRef = Constant.getValRef(subChannel,
                Constant.ATTRIBUTE_REF_STATE);
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
