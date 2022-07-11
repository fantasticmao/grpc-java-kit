package cn.fantasticmao.grpckit.nameresolver.statik;

import cn.fantasticmao.grpckit.ServiceDiscovery;
import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.support.Constant;
import io.grpc.EquivalentAddressGroup;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A static config based {@link ServiceDiscovery}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-11
 */
class StaticServiceDiscovery extends ServiceDiscovery {
    private final String authority;
    private final Map<String, List<InetSocketAddress>> serverMap;
    private final String appName;

    StaticServiceDiscovery(String authority, Map<String, List<InetSocketAddress>> serverMap,
                           String appName) {
        this.authority = authority;
        this.serverMap = serverMap;
        this.appName = appName;
    }

    @Override
    public String getServiceAuthority() {
        return this.authority;
    }

    @Override
    public void start(Listener2 listener) {
        List<InetSocketAddress> serverList = this.serverMap.get(this.appName);
        final List<ServiceMetadata> serviceMetadataList = new ArrayList<>(serverList.size());
        for (InetSocketAddress server : serverList) {
            ServiceMetadata serviceMetadata = new ServiceMetadata(server.getAddress(), server.getPort(),
                ServiceMetadata.DEFAULT_WEIGHT, ServiceMetadata.DEFAULT_TAG, this.appName, Constant.VERSION);
            serviceMetadataList.add(serviceMetadata);
        }

        List<EquivalentAddressGroup> servers = serviceMetadataList.stream()
            .map(ServiceMetadata::toAddressGroup)
            .collect(Collectors.toList());
        ResolutionResult result = ResolutionResult.newBuilder()
            .setAddresses(servers)
            .build();
        listener.onResult(result);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void shutdown() {
    }
}
