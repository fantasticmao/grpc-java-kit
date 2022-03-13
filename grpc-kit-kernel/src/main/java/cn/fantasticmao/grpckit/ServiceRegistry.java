package cn.fantasticmao.grpckit;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * Register service instance, the implementation is independent of gRPC.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public abstract class ServiceRegistry implements Closeable {

    /**
     * Register service instance.
     *
     * @param serviceName service name
     * @param address     service address
     * @return if succeed
     */
    public abstract boolean doRegister(String serviceName, InetSocketAddress address);

    @Override
    public abstract void close();

}
