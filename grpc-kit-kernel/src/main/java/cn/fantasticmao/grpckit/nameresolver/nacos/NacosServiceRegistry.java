package cn.fantasticmao.grpckit.nameresolver.nacos;

import cn.fantasticmao.grpckit.Constant;
import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.ServiceRegistry;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.URI;

/**
 * @author harrison
 * @date 2022/4/20
 */
public class NacosServiceRegistry extends ServiceRegistry {

    /**
     * Register server address
     */
    private final String serverAddress;

    private final String defaultNamespace = "public";

    /**
     * you can create yourself namespace on <a href="http://localhost:8848/nacos/">Nacos</a>,
     * and this way need <b>namespaceId</b>
     */
    private final String namespaceId;

    public NacosServiceRegistry(URI serviceUri) {
        this.serverAddress = serviceUri.getAuthority();
        this.namespaceId = "yourself namespace create on nacos";
    }

    @Override
    public boolean doRegister(ServiceMetadata metadata) {
        try {
            NamingService naming = NamingServiceHolder.get(serverAddress, defaultNamespace);
            Instance instance = new Instance();
            instance.setServiceName(metadata.getAppName());
            instance.setIp(metadata.getHost());
            instance.setPort(metadata.getPort());
            instance.setWeight(metadata.getWeight());
            instance.addMetadata("tag", metadata.getTag());
            instance.addMetadata("version", metadata.getVersion());
            naming.registerInstance(metadata.getAppName(), instance);
            return true;
        } catch (NacosException e) {
            String errorMsg = String.format("nacos registry instance error, metadata = %s", Constant.GSON.toJson(metadata));
            throw new GrpcKitException(errorMsg, e);
        }
    }

    @Override
    public void shutdown() {

    }

}
