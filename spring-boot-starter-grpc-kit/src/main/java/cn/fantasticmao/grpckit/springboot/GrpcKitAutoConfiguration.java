package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * Enable auto-configuration for gRPC applications.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-boot/docs/2.5.1/reference/html/features.html#features.developing-auto-configuration">Creating Your Own Auto-configuration</a>
 * @since 2022-04-03
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class GrpcKitAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("grpc-kit")
    public GrpcKitConfig grpcKitConfig() {
        return new GrpcKitConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcServerContainer grpcServerContainer() {
        return new GrpcServerContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcStubBeanPostProcessor grpcStubBeanPostProcessor() {
        return new GrpcStubBeanPostProcessor();
    }
}
