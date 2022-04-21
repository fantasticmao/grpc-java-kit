package cn.fantasticmao.grpckit.nameresolver.nacos;

import cn.fantasticmao.grpckit.ServiceRegistry;
import cn.fantasticmao.grpckit.ServiceRegistryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * @author harrison
 * @date 2022/4/20
 */
public class NacosServiceRegistryProvider extends ServiceRegistryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosServiceRegistryProvider.class);
    private static final String SCHEME = "nacos";

    @Nullable
    @Override
    public ServiceRegistry newServiceRegistry(URI serviceUri) {
        if (!SCHEME.equals(serviceUri.getScheme())) {
            return null;
        }
        return new NacosServiceRegistry(serviceUri);
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.alibaba.nacos.client.naming.NacosNamingService");
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unable to load nacos NameResolver, can't found dependency: NacosNamingService", e);
            return false;
        }
    }

    @Override
    public int priority() {
        return DEFAULT_PRIORITY + 2;
    }
}
