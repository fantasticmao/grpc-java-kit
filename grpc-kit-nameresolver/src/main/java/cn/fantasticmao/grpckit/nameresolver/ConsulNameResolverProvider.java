package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolver;

import java.net.URI;

/**
 * ConsulNameResolverProvider
 *
 * @author maomao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolverProvider
 * @since 2021-08-03
 */
public class ConsulNameResolverProvider extends AbstractNameResolverProvider {
    public static final String VM_OPTION = "io.grpc.NameResolverProvider.switch.consul";

    public ConsulNameResolverProvider() {
    }

    @Override
    protected String getVmOptionKey() {
        return VM_OPTION;
    }

    @Override
    protected boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }

        // TODO
        return true;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        // TODO
        return null;
    }

    @Override
    public String getDefaultScheme() {
        // TODO
        return null;
    }
}
