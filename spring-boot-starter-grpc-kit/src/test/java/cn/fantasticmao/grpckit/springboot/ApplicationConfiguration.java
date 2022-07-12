package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.springboot.factory.GrpcKitServerBuilderFactory;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * ApplicationConfiguration
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-04
 */
@SpringBootApplication
public class ApplicationConfiguration {

    @Bean
    public GrpcKitServerBuilderFactory grpcKitServerBuilderFactory() {
        return (builder, services) -> builder
            .addServices(services)
            .addService(ProtoReflectionService.newInstance());
    }
}
