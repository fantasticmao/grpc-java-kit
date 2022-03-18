package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ZkServiceBasedTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-19
 */
class ZkServiceBasedTest {
    private final ZkServiceBased zkService = new ZkServiceBased() {
    };

    @Test
    public void getServerPath() {
        String serverPath = zkService.getServerPath("example_service", "default");
        String expected = "/grpc-java/example_service/default/server";
        Assertions.assertEquals(expected, serverPath);
    }

    @Test
    public void newServerNodePath() {
        String serverPath = zkService.newServerNodePath("example_service", "default", "127.0.0.1", 8080);
        String expected = "/grpc-java/example_service/default/server/127.0.0.1:8080";
        Assertions.assertEquals(expected, serverPath);
    }

    @Test
    public void getClientPath() {
        String clientPath = zkService.getClientPath("example_service", "default");
        String expected = "/grpc-java/example_service/default/client";
        Assertions.assertEquals(expected, clientPath);
    }
}
