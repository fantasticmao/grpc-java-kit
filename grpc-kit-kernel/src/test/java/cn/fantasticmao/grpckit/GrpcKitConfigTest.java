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
        GrpcKitConfig config = GrpcKitConfig.getInstance();
        Assertions.assertEquals("example_service", config.getName());

        Assertions.assertEquals("dev", config.getGrpc().getGroup());
        Assertions.assertEquals(8080, config.getGrpc().getServer().getPort());
        Assertions.assertEquals(5, config.getGrpc().getServer().getWeight());
        Assertions.assertEquals("debug", config.getGrpc().getServer().getTag());
        Assertions.assertNull(config.getGrpc().getServer().getInterfaceName());
        Assertions.assertEquals(5_000, config.getGrpc().getClient().getTimeout());

        Assertions.assertEquals("zookeeper://localhost:2181", config.getNameResolver().getRegistry());
        Assertions.assertEquals(500, config.getNameResolver().getTimeout());

        Assertions.assertEquals(1, config.getLoadBalancer().getMaxFails());
        Assertions.assertEquals(30_000, config.getLoadBalancer().getFailTimeout());
    }
}
