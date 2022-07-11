package cn.fantasticmao.grpckit.nameresolver.statik;

import cn.fantasticmao.grpckit.ServiceURI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * StaticServiceURITest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-11
 */
public class StaticServiceURITest {

    @Test
    public void loadWith() {
        URI registryUri = URI.create("static://localhost");
        ServiceURI serviceURI = ServiceURI.Factory.loadWith(registryUri,
            "example_service", "default");
        URI targetUri = serviceURI.toTargetUri();
        Assertions.assertEquals("static", targetUri.getScheme());
        Assertions.assertEquals("localhost", targetUri.getHost());
        Assertions.assertEquals(-1, targetUri.getPort());
        Assertions.assertEquals("/example_service", targetUri.getPath());
    }

    @Test
    public void loadFrom() {
        URI targetUri = URI.create("static://localhost/example_service");
        ServiceURI serviceURI = ServiceURI.Factory.loadFrom(targetUri);
        Assertions.assertEquals("static", serviceURI.registryUri.getScheme());
        Assertions.assertEquals("localhost", serviceURI.registryUri.getHost());
        Assertions.assertEquals(-1, serviceURI.registryUri.getPort());
        Assertions.assertEquals("example_service", serviceURI.appName);
        Assertions.assertNull(serviceURI.appGroup);
    }

    @Test
    public void toServerMap() {
        URI registryUri = URI.create("static://localhost?foo_service=192.168.1.1:8080,192.168.1.2:9090&bar_service=192.168.1.3:8081");
        ServiceURI serviceURI = ServiceURI.Factory.loadWith(registryUri,
            "example_service", "default");
        Assertions.assertTrue(serviceURI instanceof StaticServiceURI);
        StaticServiceURI staticServiceURI = (StaticServiceURI) serviceURI;
        Map<String, List<InetSocketAddress>> serverMap = staticServiceURI.toServerMap();

        List<InetSocketAddress> fooServers = serverMap.get("foo_service");
        Assertions.assertEquals(2, fooServers.size());
        Assertions.assertEquals("192.168.1.1", fooServers.get(0).getAddress().getHostAddress());
        Assertions.assertEquals(8080, fooServers.get(0).getPort());
        Assertions.assertEquals("192.168.1.2", fooServers.get(1).getAddress().getHostAddress());
        Assertions.assertEquals(9090, fooServers.get(1).getPort());

        List<InetSocketAddress> barServers = serverMap.get("bar_service");
        Assertions.assertEquals(1, barServers.size());
        Assertions.assertEquals("192.168.1.3", barServers.get(0).getAddress().getHostAddress());
        Assertions.assertEquals(8081, barServers.get(0).getPort());
    }

}
