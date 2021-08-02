package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolver;

/**
 * ConsulNameResolver
 *
 * @author maomao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolver
 * @since 2021-08-03
 */
public class ConsulNameResolver extends NameResolver {

    @Override
    public String getServiceAuthority() {
        // TODO
        return null;
    }

    @Override
    public void shutdown() {
        // TODO
    }
}
