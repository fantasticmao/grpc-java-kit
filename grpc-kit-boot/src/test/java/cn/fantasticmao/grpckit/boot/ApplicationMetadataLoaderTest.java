package cn.fantasticmao.grpckit.boot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * ApplicationMetadataLoaderTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
public class ApplicationMetadataLoaderTest {

    @Test
    public void load() {
        ServiceLoader<ApplicationMetadataLoader> serviceLoader
            = ServiceLoader.load(ApplicationMetadataLoader.class);
        List<ApplicationMetadata> metadataList = serviceLoader.stream()
            .map(ServiceLoader.Provider::get)
            .map(metadataLoader -> metadataLoader.load(null))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        Assertions.assertNotNull(metadataList);
        Assertions.assertEquals(1, metadataList.size());

        ApplicationMetadata metadata = metadataList.get(0);
        Assertions.assertEquals("unit_test", metadata.getName());
        Assertions.assertArrayEquals(new String[]{"unit_test.GreeterService"}, metadata.getServices().toArray());
    }
}
