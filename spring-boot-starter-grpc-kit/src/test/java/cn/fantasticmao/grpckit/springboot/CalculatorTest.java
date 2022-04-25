package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.proto.Calculator;
import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * CalculatorTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-04
 */
@SpringBootTest(classes = ApplicationConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CalculatorTest {
    @Autowired
    private CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorStub;
    @GrpcClient
    private CalculatorServiceGrpc.CalculatorServiceStub stub;
    @GrpcClient
    private CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub;
    @GrpcClient
    private CalculatorServiceGrpc.CalculatorServiceFutureStub futureStub;

    @Test
    public void plus() {
        Calculator.Input input = Calculator.Input.newBuilder()
            .setA(1)
            .setB(2)
            .build();
        Calculator.Output output = calculatorStub.plus(input);
        Assertions.assertEquals(3, output.getResult());
    }

    @Test
    public void minus() {
        Calculator.Input input = Calculator.Input.newBuilder()
            .setA(3)
            .setB(2)
            .build();
        Calculator.Output output = calculatorStub.minus(input);
        Assertions.assertEquals(1, output.getResult());
    }
}
