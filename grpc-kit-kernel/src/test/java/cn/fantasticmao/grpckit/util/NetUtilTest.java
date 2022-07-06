package cn.fantasticmao.grpckit.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * NetUtilTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-21
 */
public class NetUtilTest {

    @Test
    public void getLocalAddress() throws IOException {
        InetAddress address = NetUtil.getLocalAddress(null);
        Assertions.assertNotNull(address);
        Assertions.assertTrue(address.isReachable(100));
    }
}
