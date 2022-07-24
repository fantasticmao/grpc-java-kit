package cn.fantasticmao.grpckit.boot.metadata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ApplicationMetadataCacheTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-05-02
 */
public class ApplicationMetadataCacheTest {

    @Test
    public void getByServiceName() {
        String serviceName = "unit_test.GreeterService";
        ApplicationMetadata metadata = ApplicationMetadataCache.getInstance().getByServiceName(serviceName);

        Assertions.assertEquals("unit_test", metadata.getName());
        Assertions.assertArrayEquals(new String[]{serviceName}, metadata.getServices().toArray());
    }
}
