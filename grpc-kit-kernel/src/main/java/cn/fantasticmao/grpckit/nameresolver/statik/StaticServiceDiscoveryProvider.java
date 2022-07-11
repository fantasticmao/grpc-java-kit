package cn.fantasticmao.grpckit.nameresolver.statik;

import cn.fantasticmao.grpckit.ServiceDiscoveryProvider;
import cn.fantasticmao.grpckit.ServiceURI;
import io.grpc.NameResolver;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * A provider for {@link StaticServiceDiscovery}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-11
 */
public class StaticServiceDiscoveryProvider extends ServiceDiscoveryProvider {
    private static final String SCHEME = "static";

    public StaticServiceDiscoveryProvider() {
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(ServiceURI serviceUri, NameResolver.Args args) {
        if (!SCHEME.equalsIgnoreCase(serviceUri.registryUri.getScheme())
            || !(serviceUri instanceof StaticServiceURI)) {
            return null;
        }
        String authority = serviceUri.registryUri.getAuthority();
        Map<String, List<InetSocketAddress>> serverMap = ((StaticServiceURI) serviceUri).toServerMap();
        String appName = serviceUri.appName;
        return new StaticServiceDiscovery(authority, serverMap, appName);
    }

    @Override
    public String getDefaultScheme() {
        return SCHEME;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return DEFAULT_PRIORITY - 1;
    }
}
