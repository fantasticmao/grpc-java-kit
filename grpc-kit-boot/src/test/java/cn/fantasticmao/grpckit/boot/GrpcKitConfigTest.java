package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * GrpcKitConfigTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-21
 */
public class GrpcKitConfigTest {

    @Test
    public void loadAndParse() {
        GrpcKitConfig config = GrpcKitConfig.loadAndParse("grpc-kit-example.yml");

        Assertions.assertEquals("dev", config.getGroup());
        Assertions.assertEquals(8080, config.getServer().getPort());
        Assertions.assertEquals(100, config.getServer().getWeight());
        Assertions.assertEquals("debug", config.getServer().getTag());
        Assertions.assertEquals("en0", config.getServer().getInterfaceName());
        Assertions.assertEquals("debug", config.getClient().getTag());
        Assertions.assertEquals(5_000, config.getClient().getTimeout());

        Assertions.assertEquals("zookeeper://localhost:2181", config.getNameResolver().getRegistry());

        Assertions.assertEquals(ServiceLoadBalancer.Policy.PICK_FIRST, ServiceLoadBalancer.Policy.of(config.getLoadBalancer().getPolicy()));
        Assertions.assertEquals(1, config.getLoadBalancer().getMaxFails());
        Assertions.assertEquals(30_000, config.getLoadBalancer().getFailTimeout());
    }
}
