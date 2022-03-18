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
        Assertions.assertEquals("dev", config.getGrpc().getGroup());

        Assertions.assertEquals(8080, config.getGrpc().getServer().getPort());
        Assertions.assertEquals(70, config.getGrpc().getServer().getWeight());
        Assertions.assertEquals("debug", config.getGrpc().getServer().getTag());
        Assertions.assertEquals("", config.getGrpc().getServer().getInterfaceName());

        Assertions.assertEquals(5000, config.getGrpc().getClient().getTimeout());

        Assertions.assertEquals("localhost:2181", config.getNameResolver().getZookeeper().getConnectString());
    }
}
