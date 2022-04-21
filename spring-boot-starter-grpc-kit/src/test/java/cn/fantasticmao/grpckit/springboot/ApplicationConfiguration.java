package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.boot.GrpcKitChannelBuilder;
import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import cn.fantasticmao.grpckit.boot.GrpcKitStubFactory;
import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import io.grpc.Channel;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Channel unitTestChannel(@Autowired GrpcKitConfig config) {
        return GrpcKitChannelBuilder.forConfig("unit_test_spring_boot", config)
            .usePlaintext()
            .build();
    }

    @Bean
    public CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorStub(@Autowired Channel channel,
                                                                              @Autowired GrpcKitConfig config) {
        return GrpcKitStubFactory.newStub(CalculatorServiceGrpc.CalculatorServiceBlockingStub.class, channel, config);
    }
}
