package cn.fantasticmao.grpckit.nameresolver.dns;

import cn.fantasticmao.grpckit.ServiceURI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * DnsServiceURITest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-09
 */
public class DnsServiceURITest {

    @Test
    public void loadWith() {
        URI registryUri = URI.create("dns://8.8.8.8");
        ServiceURI serviceURI = ServiceURI.Factory.loadWith(registryUri,
            "example_service", "default");
        URI targetUri = serviceURI.toTargetUri();
        Assertions.assertEquals("dns", targetUri.getScheme());
        Assertions.assertEquals("8.8.8.8", targetUri.getHost());
        Assertions.assertEquals(-1, targetUri.getPort());
        Assertions.assertEquals("/example_service", targetUri.getPath());
    }

    @Test
    public void loadFrom() {
        URI targetUri = URI.create("dns://8.8.8.8/example_service");
        ServiceURI serviceURI = ServiceURI.Factory.loadFrom(targetUri);
        Assertions.assertEquals("dns", serviceURI.registryUri.getScheme());
        Assertions.assertEquals("8.8.8.8", serviceURI.registryUri.getHost());
        Assertions.assertEquals(-1, serviceURI.registryUri.getPort());
        Assertions.assertEquals("example_service", serviceURI.appName);
        Assertions.assertNull(serviceURI.appGroup);
    }
}
