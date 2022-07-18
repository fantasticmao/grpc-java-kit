package cn.fantasticmao.grpckit.springboot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enable auto-configuration for gRPC applications.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-boot/docs/2.5.1/reference/html/features.html#features.developing-auto-configuration">Creating Your Own Auto-configuration</a>
 * @since 2022-04-03
 */
@EnableConfigurationProperties(GrpcKitConfigProperties.class)
@Configuration
public class GrpcKitAutoConfiguration {
    public static final String BEAN_NAME_GRPC_KIT_SERVER = "__grpcKitServer__";

    @Bean
    @ConditionalOnMissingBean
    public GrpcStubBeanPostProcessor grpcStubBeanPostProcessor() {
        return new GrpcStubBeanPostProcessor();
    }
}
