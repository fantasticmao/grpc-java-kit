package cn.fantasticmao.grpckit.nameresolver.consul;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

/**
 * ConsulNameResolverProvider
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolverProvider
 * @since 2021-08-03
 */
public class ConsulNameResolverProvider extends NameResolverProvider {
    private static final String SCHEME = "consul";

    public ConsulNameResolverProvider() {
    }

    @Override
    protected boolean isAvailable() {
        // TODO
        return true;
    }

    @Override
    protected int priority() {
        // less than the default value
        return 5 - 1;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        if (SCHEME.equalsIgnoreCase(targetUri.getScheme())) {
            // TODO
            return new ConsulNameResolver();
        } else {
            return null;
        }
    }

    @Override
    public String getDefaultScheme() {
        // TODO
        return SCHEME;
    }
}
