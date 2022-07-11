package cn.fantasticmao.grpckit.nameresolver.statik;

import cn.fantasticmao.grpckit.ServiceDiscoveryProvider;
import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.ServiceURI;
import io.grpc.NameResolver;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.*;

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
        if (!SCHEME.equalsIgnoreCase(serviceUri.registryUri.getScheme())) {
            return null;
        }
        String authority = serviceUri.registryUri.getAuthority();
        String query = serviceUri.registryUri.getQuery();
        String appName = serviceUri.appName;
        Map<String, List<InetSocketAddress>> serverMap = this.mapServerFromQuery(query);
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

    private Map<String, List<InetSocketAddress>> mapServerFromQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, List<InetSocketAddress>> serverMap = new HashMap<>();
        String[] servers = query.split("&");
        for (String server : servers) {
            String[] nameToAuthorities = server.split("=");
            if (nameToAuthorities.length != 2 || nameToAuthorities[0].isBlank()
                || nameToAuthorities[1].isBlank()) {
                throw new IllegalArgumentException("Illegal syntax in StaticServiceURI: " + server);
            }

            String serverName = nameToAuthorities[0];
            if (!serverMap.containsKey(serverName)) {
                serverMap.put(serverName, new LinkedList<>());
            }

            String[] serverAuthorities = nameToAuthorities[1].split(",");
            for (String authority : serverAuthorities) {
                String[] hostAndPort = authority.split(":");
                if (hostAndPort.length > 2) {
                    throw new IllegalArgumentException("Illegal syntax in StaticServiceURI: " + server);
                }
                String host = hostAndPort[0];
                int port = hostAndPort.length > 1
                    ? Integer.parseInt(hostAndPort[1])
                    : ServiceMetadata.DEFAULT_PORT;
                serverMap.get(serverName).add(new InetSocketAddress(host, port));
            }
        }
        return serverMap;
    }
}
