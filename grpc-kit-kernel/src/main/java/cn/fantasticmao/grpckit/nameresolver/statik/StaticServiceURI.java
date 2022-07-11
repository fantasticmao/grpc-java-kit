package cn.fantasticmao.grpckit.nameresolver.statik;

import cn.fantasticmao.grpckit.ServiceURI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A static config based {@link ServiceURI}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-11
 */
public class StaticServiceURI extends ServiceURI {

    public StaticServiceURI(URI registryUri, String appName, @Nullable String appGroup) {
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
