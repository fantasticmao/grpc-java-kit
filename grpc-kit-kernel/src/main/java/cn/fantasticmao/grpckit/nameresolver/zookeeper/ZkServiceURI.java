package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.ServiceURI;
import cn.fantasticmao.grpckit.ServiceURILoader;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A ZooKeeper based {@link ServiceURI}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-07
 */
public class ZkServiceURI extends ServiceURI {

    public ZkServiceURI(URI registryUri, String appName, String appGroup) {
        super(registryUri, appName, appGroup);
    }

    @Override
    public URI toTargetUri() {
        final String path = String.format("/%s/%s/servers", super.appName, super.appGroup);
        try {
            return new URI(super.registryUri.getScheme(), super.registryUri.getUserInfo(),
                super.registryUri.getHost(), super.registryUri.getPort(), path,
                super.registryUri.getQuery(), super.registryUri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static class Loader implements ServiceURILoader {
        private static final String SCHEME = "zookeeper";

        @Nullable
        @Override
        public ServiceURI with(URI registryUri, String appName, String appGroup) {
            if (!SCHEME.equalsIgnoreCase(registryUri.getScheme())) {
                return null;
            }
            return new ZkServiceURI(registryUri, appName, appGroup);
        }

        @Nullable
        @Override
        public ServiceURI from(URI targetUri) {
            if (!SCHEME.equalsIgnoreCase(targetUri.getScheme())) {
                return null;
            }

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
            String appGroup = elements[2];
            return new ZkServiceURI(registryUri, appName, appGroup);
        }
    }
}
