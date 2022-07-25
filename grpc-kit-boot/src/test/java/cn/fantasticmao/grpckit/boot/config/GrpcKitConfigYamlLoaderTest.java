package cn.fantasticmao.grpckit.boot.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * GrpcKitConfigYamlLoaderTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-25
 */
public class GrpcKitConfigYamlLoaderTest {
    private final GrpcKitConfigYamlLoader.RelaxedBindingPropertyUtils propertyUtils
        = new GrpcKitConfigYamlLoader.RelaxedBindingPropertyUtils();

    @Test
    public void convertName() {
        String actual = propertyUtils.convertName("helloWorld");
        Assertions.assertEquals("helloWorld", actual);

        actual = propertyUtils.convertName("hello-world");
        Assertions.assertEquals("helloWorld", actual);

        actual = propertyUtils.convertName("-hello-world-");
        Assertions.assertEquals("HelloWorld", actual);

        actual = propertyUtils.convertName("hello--world");
        Assertions.assertEquals("helloWorld", actual);

        actual = propertyUtils.convertName("hello_world");
        Assertions.assertEquals("helloWorld", actual);

        actual = propertyUtils.convertName("-hello__world");
        Assertions.assertEquals("HelloWorld", actual);
    }

}
