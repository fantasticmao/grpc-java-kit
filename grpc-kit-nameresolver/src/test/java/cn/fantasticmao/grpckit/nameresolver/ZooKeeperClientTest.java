package cn.fantasticmao.grpckit.nameresolver;

import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * ZooKeeperClientTest
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021/8/5
 */
public class ZooKeeperClientTest {

    @Test
    public void test() throws KeeperException, InterruptedException {
        ZooKeeperClient client = ZooKeeperClient.getInstance();
        byte[] data = client.getDataByPath("/cn/fantasticmao/grpc_java_kit");
        System.out.println(new String(data, StandardCharsets.UTF_8));
    }

}