package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Integrate {@link GrpcKitConfig} into the Spring configuration.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-18
 */
@ConfigurationProperties("grpc-kit")
public class GrpcKitConfigProperties extends GrpcKitConfig {

}
