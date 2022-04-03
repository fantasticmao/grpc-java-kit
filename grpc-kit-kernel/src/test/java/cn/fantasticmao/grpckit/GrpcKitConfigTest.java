package cn.fantasticmao.grpckit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * GrpcKitConfigTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-19
 */
public class GrpcKitConfigTest {

    @Test
    public void getInstance() {
        GrpcKitConfig config = GrpcKitConfig.loadAndParse("grpc-kit.yaml");
        Assertions.assertEquals("unit-test", config.getName());
        Assertions.assertEquals("dev", config.getGroup());

        Assertions.assertEquals(8080, config.getGrpc().getServer().getPort());
        Assertions.assertEquals(100, config.getGrpc().getServer().getWeight());
        Assertions.assertEquals("debug", config.getGrpc().getServer().getTag());
        Assertions.assertNull(config.getGrpc().getServer().getInterfaceName());
        Assertions.assertEquals("debug", config.getGrpc().getStub().getTag());
        Assertions.assertEquals(5_000, config.getGrpc().getStub().getTimeout());

        Assertions.assertEquals("zookeeper://localhost:2181", config.getNameResolver().getRegistry());

        Assertions.assertEquals(ServiceLoadBalancer.Policy.PICK_FIRST, ServiceLoadBalancer.Policy.of(config.getLoadBalancer().getPolicy()));
        Assertions.assertEquals(1, config.getLoadBalancer().getMaxFails());
        Assertions.assertEquals(30_000, config.getLoadBalancer().getFailTimeout());
    }
}
