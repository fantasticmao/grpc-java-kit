package cn.fantasticmao.grpckit.nameresolver.statik;

import cn.fantasticmao.grpckit.ServiceMetadata;
import cn.fantasticmao.grpckit.ServiceURI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * A static config based {@link ServiceURI}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see StaticServiceDiscovery
 * @see StaticServiceDiscoveryProvider
 * @since 2022-07-11
 */
public class StaticServiceURI extends ServiceURI {

    StaticServiceURI(URI registryUri, String appName, @Nullable String appGroup) {
        super(registryUri, appName, appGroup);
    }

    @Override
    public URI toTargetUri() {
        final String path = String.format("/%s", super.appName);
        try {
            return new URI(super.registryUri.getScheme(), super.registryUri.getUserInfo(),
                super.registryUri.getHost(), super.registryUri.getPort(), path,
                super.registryUri.getQuery(), super.registryUri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    Map<String, List<InetSocketAddress>> toServerMap() {
        if (registryUri.getQuery() == null || registryUri.getQuery().isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, List<InetSocketAddress>> serverMap = new HashMap<>();
        String[] servers = registryUri.getQuery().split("&");
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

    public static class Factory implements ServiceURI.Factory {

        @Nonnull
        @Override
        public String getScheme() {
            return "static";
        }

        @Nonnull
        @Override
        public ServiceURI with(URI registryUri, String appName, String appGroup) {
            return new StaticServiceURI(registryUri, appName, appGroup);
        }

        @Nonnull
        @Override
        public ServiceURI from(URI targetUri) {
            URI registryUri;
            try {
                registryUri = new URI(targetUri.getScheme(), targetUri.getUserInfo(),
                    targetUri.getHost(), targetUri.getPort(), "",
                    targetUri.getQuery(), targetUri.getFragment());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }

            String[] elements = targetUri.getPath().split("/");
            String appName = elements[1];
            return new StaticServiceURI(registryUri, appName, null);
        }
    }
}
