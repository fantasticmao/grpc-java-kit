package cn.fantasticmao.grpckit.nameresolver.nacos;

import cn.fantasticmao.grpckit.ServiceDiscoveryProvider;
import io.grpc.NameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * @author harrison
 * @date 2022/4/20
 */
public class NacosServiceDiscoveryProvider extends ServiceDiscoveryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceDiscoveryProvider.class);

    private static final String SCHEME = "nacos";

    @Override
    public NameResolver newNameResolver(URI serviceUri, NameResolver.Args args) {
        if (SCHEME.equalsIgnoreCase(serviceUri.getScheme())) {
            return new NacosServiceDiscovery(serviceUri, args);
        }
        return null;
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        try {
            Class.forName("com.alibaba.nacos.client.naming.NacosNamingService");
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unable to load nacos NameResolver, can't found dependency: NacosNamingService", e);
            return false;
        }
    }

    @Override
    protected int priority() {
        // priority greater than zk
        return DEFAULT_PRIORITY + 2;
    }
}
