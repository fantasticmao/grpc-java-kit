package cn.fantasticmao.grpckit.springboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Grpc Kit Auto Configuration.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-boot/docs/2.5.1/reference/html/features.html#features.developing-auto-configuration">Creating Your Own Auto-configuration</a>
 * @since 2022-04-03
 */
@Configuration
public class GrpcKitAutoConfiguration {
    @Value("${cn.fantasticmao.grpckit.config:grpc-kit.yaml}")
    private String configPath;

    @Bean
    @ConditionalOnMissingBean
    public GrpcClientBeanPostProcessor clientBeanPostProcessor() {
        return new GrpcClientBeanPostProcessor(configPath);
    }
}
