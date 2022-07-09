package cn.fantasticmao.grpckit.nameresolver.dns;

import cn.fantasticmao.grpckit.ServiceURI;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A DNS based {@link ServiceURI}.
 *
 * <p>The capability of grouping applications will be not work.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-07
 */
public class DnsServiceURI extends ServiceURI {

    public DnsServiceURI(URI registryUri, String appName, String appGroup) {
        super(registryUri, appName, appGroup);
    }

    @Override
    public URI toTargetUri() {
        String path = String.format("/%s", super.appName);
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
            return "dns";
        }

        @Nonnull
        @Override
        public ServiceURI with(URI registryUri, String appName, String appGroup) {
            return new DnsServiceURI(registryUri, appName, appGroup);
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
            return new DnsServiceURI(registryUri, appName, null);
        }
    }
}
