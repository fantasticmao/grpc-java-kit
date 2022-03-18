package cn.fantasticmao.grpckit.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * NetUtilTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-18
 */
class NetUtilTest {

    @Test
    public void getLocalAddress() throws IOException {
        InetAddress address = NetUtil.getLocalAddress();
        Assertions.assertNotNull(address);
        Assertions.assertTrue(address.isReachable(100));
    }
}
