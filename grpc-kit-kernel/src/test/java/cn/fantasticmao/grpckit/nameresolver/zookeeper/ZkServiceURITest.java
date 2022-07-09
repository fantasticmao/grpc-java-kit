package cn.fantasticmao.grpckit.nameresolver.zookeeper;

import cn.fantasticmao.grpckit.ServiceURI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * ZkServiceURITest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-09
 */
public class ZkServiceURITest {

    @Test
    public void loadWith() {
        URI registryUri = URI.create("zookeeper://zk.example.com:2181");
        ServiceURI serviceURI = ServiceURI.Factory.loadWith(registryUri,
            "example_service", "default");
        URI targetUri = serviceURI.toTargetUri();
        Assertions.assertEquals("zookeeper", targetUri.getScheme());
        Assertions.assertEquals("zk.example.com", targetUri.getHost());
        Assertions.assertEquals(2181, targetUri.getPort());
        Assertions.assertEquals("/example_service/default", targetUri.getPath());
    }

    @Test
    public void loadFrom() {
        URI targetUri = URI.create("zookeeper://zk.example.com:2181/example_service/default");
        ServiceURI serviceURI = ServiceURI.Factory.loadFrom(targetUri);
        Assertions.assertEquals("zookeeper", serviceURI.registryUri.getScheme());
        Assertions.assertEquals("zk.example.com", serviceURI.registryUri.getHost());
        Assertions.assertEquals(2181, serviceURI.registryUri.getPort());
        Assertions.assertEquals("example_service", serviceURI.appName);
        Assertions.assertEquals("default", serviceURI.appGroup);
    }
}
