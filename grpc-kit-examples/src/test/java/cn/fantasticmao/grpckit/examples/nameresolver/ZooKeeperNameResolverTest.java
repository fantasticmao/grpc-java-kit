package cn.fantasticmao.grpckit.examples.nameresolver;

import cn.fantasticmao.grpckit.nameresolver.ConsulNameResolverProvider;
import cn.fantasticmao.grpckit.nameresolver.ServiceConfigBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * ZooKeeperNameResolverTest
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021-08-03
 */
public class ZooKeeperNameResolverTest {

    @BeforeAll
    public static void beforeAll() {
        System.setProperty(ConsulNameResolverProvider.VM_OPTION, Boolean.FALSE.toString());
    }

    @Test
    public void zooKeeperNameResolver() {
        final String host = "localhost";
        final int port = 50051;
        final String zkHost = "localhost:2181";

        // TODO
        Map<String, ?> serviceConfig = ServiceConfigBuilder
            .forZooKeeper(zkHost)
            .build();
        try {
            ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                //.defaultServiceConfig(serviceConfig)
                .usePlaintext()
                .build();
        } finally {

        }
    }
}
