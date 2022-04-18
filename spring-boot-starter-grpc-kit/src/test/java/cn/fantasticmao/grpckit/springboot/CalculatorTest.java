package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import cn.fantasticmao.grpckit.proto.Input;
import cn.fantasticmao.grpckit.proto.Output;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * CalculatorTest
 *
 * @author fantasticmao
 * @since 2022-04-04
 */
@SpringBootTest(classes = ApplicationConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CalculatorTest {
    @Autowired
    private CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorStub;

    @Test
    public void plus() {
        Input input = Input.newBuilder()
            .setA(1)
            .setB(2)
            .build();
        Output output = calculatorStub.plus(input);
        Assertions.assertEquals(3, output.getResult());
    }

    @Test
    public void minus() {
        Input input = Input.newBuilder()
            .setA(3)
            .setB(2)
            .build();
        Output output = calculatorStub.minus(input);
        Assertions.assertEquals(1, output.getResult());
    }
}
