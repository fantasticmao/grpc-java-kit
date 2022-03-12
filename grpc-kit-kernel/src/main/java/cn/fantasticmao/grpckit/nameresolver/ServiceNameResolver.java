package cn.fantasticmao.grpckit.nameresolver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/**
 * ServiceNameResolver
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-08-06
 */
public interface ServiceNameResolver {

    interface Registry {

        void doRegistry(String serviceName, SocketAddress address);

    }

    interface Discovery {

        List<InetSocketAddress> lookup(String serviceName);

    }
}
