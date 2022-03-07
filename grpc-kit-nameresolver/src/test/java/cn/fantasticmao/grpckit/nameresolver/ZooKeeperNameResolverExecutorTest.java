package cn.fantasticmao.grpckit.nameresolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * ZooKeeperNameResolverExecutorTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021/8/5
 */
@Disabled("Need ZooKeeper dependencies")
public class ZooKeeperNameResolverExecutorTest {

    @Test
    public void test() throws Exception {
        final String path = "/app/fantasticmao/cn";
        final String host = "127.0.0.1";

        ZooKeeperNameResolverExecutor client = ZooKeeperNameResolverExecutor.getInstance();
        client.doRegistry(path, host);

        String data = client.lookup(path);
        Assertions.assertEquals(host, data);
    }

}