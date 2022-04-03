package cn.fantasticmao.grpckit.springboot;

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
    private CalculatorClient calculatorClient;

    @Test
    public void add() {
        int result = calculatorClient.add(1, 2);
        Assertions.assertEquals(3, result);
    }
}
