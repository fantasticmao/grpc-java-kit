package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitFactory;
import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import io.grpc.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * ApplicationConfiguration
 *
 * @author fantasticmao
 * @since 2022-04-04
 */
@SpringBootApplication
public class ApplicationConfiguration {

    @Bean
    public Channel unitTestChannel(@Autowired GrpcKitFactory factory) {
        return factory.newChannel("unit_test_spring_boot");
    }

    @Bean
    public CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorStub(@Autowired GrpcKitFactory factory,
                                                                              @Autowired Channel channel) {
        return factory.newStub(CalculatorServiceGrpc.CalculatorServiceBlockingStub.class, channel);
    }
}
