package cn.fantasticmao.grpckit.nameresolver.nacos;


import cn.fantasticmao.grpckit.Constant;
import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceDiscovery;
import cn.fantasticmao.grpckit.support.AttributeUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author harrison
 * @date 2022/4/20
 */
public class NacosServiceDiscovery extends ServiceDiscovery {

    /**
     * Register server address
     */
    private final String serverAddress;

    private final String appName;

    private final String defaultNamespace = "public";

    private final String namespaceId;

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private Listener2 listener;

    private final AtomicBoolean resolving = new AtomicBoolean(false);

    public NacosServiceDiscovery(URI serviceUri, NameResolver.Args args) {
        this.serverAddress = serviceUri.getAuthority();
        this.appName = this.parseAppName(serviceUri.getPath());
        this.namespaceId = "yourself namespace create on nacos";
    }

    @Override
    public String getServiceAuthority() {
        return serverAddress;
    }

    @Override
    public void start(Listener2 listener) {
        if (this.listener != null) {
            LOGGER.warn("Already started {}", this.getClass().getName());
            return;
        }
        this.listener = listener;
        this.resolve();
    }

    @Override
    public void refresh() {
        if (this.listener == null) {
            LOGGER.warn("Not started {}", this.getClass().getName());
            return;
        }
        this.resolve();
    }

    @Override
    public void shutdown() {

    }

    private void resolve() {
        if (this.resolving.get()) {
            LOGGER.warn("Already resolved {}", this.getClass().getName());
            return;
        }
        this.resolving.compareAndSet(false, true);
        try {
            this.lookUp();
        } finally {
            this.resolving.compareAndSet(true, false);
        }
    }

    /**
     * 服务发现
     */
    private void lookUp() {
        // build service metadata
        NamingService namingService = NamingServiceHolder.get(serverAddress, defaultNamespace);
        List<Instance> instances;
        try {
            instances = namingService.getAllInstances(appName);
        } catch (NacosException e) {
            throw new GrpcKitException("NamingService get all instances error.", e);
        }
        if (instances == null || instances.isEmpty()) {
            throw new GrpcKitException("NamingService do not discovery any instances");
        }
        List<EquivalentAddressGroup> servers = instances.stream()
            .map(this::toAddressGroup)
            .collect(Collectors.toList());
        ResolutionResult result = ResolutionResult.newBuilder()
            .setAddresses(servers)
            .build();
        this.listener.onResult(result);
    }

    private String parseAppName(String path) {
        String[] pathArr = path.split(Constant.URI_SPILT);
        if (pathArr.length < 2) {
            String errorMsg = String.format("parse app name error from %s", path);
            throw new GrpcKitException(errorMsg);
        }
        return pathArr[1];
    }

    private EquivalentAddressGroup toAddressGroup(Instance instance) {
        InetSocketAddress address = new InetSocketAddress(instance.getIp(), instance.getPort());
        Attributes attributes = Attributes.newBuilder()
            .set(AttributeUtil.KEY_WEIGHT, (int)instance.getWeight())
            .set(AttributeUtil.KEY_TAG, instance.getMetadata().get("tag"))
            .build();
        return new EquivalentAddressGroup(address, attributes);
    }
}
