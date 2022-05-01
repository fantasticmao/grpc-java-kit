package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.proto.Calculator;
import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * CalculatorTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-04
 */
@SpringBootTest(classes = ApplicationConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CalculatorTest {
    @GrpcClient
    private CalculatorServiceGrpc.CalculatorServiceStub stub;
    @GrpcClient
    private CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub;
    @GrpcClient
    private CalculatorServiceGrpc.CalculatorServiceFutureStub futureStub;

    @Test
    public void inject() {
        Assertions.assertNotNull(stub);
        Assertions.assertNotNull(blockingStub);
        Assertions.assertNotNull(futureStub);

        Assertions.assertEquals(stub.getChannel(), blockingStub.getChannel());
        Assertions.assertEquals(blockingStub.getChannel(), futureStub.getChannel());
        Assertions.assertEquals(futureStub.getChannel(), stub.getChannel());
    }

    @Test
    public void plus() {
        Calculator.Input input = Calculator.Input.newBuilder()
            .setA(1)
            .setB(2)
            .build();
        Calculator.Output output = blockingStub.plus(input);
        Assertions.assertEquals(3, output.getResult());
    }

    @Test
    public void minus() {
        Calculator.Input input = Calculator.Input.newBuilder()
            .setA(3)
            .setB(2)
            .build();
        stub.minus(input, new StreamObserver<>() {
            @Override
            public void onNext(Calculator.Output output) {
                Assertions.assertEquals(1, output.getResult());
            }

            @Override
            public void onError(Throwable t) {
                Assertions.fail();
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    @Test
    public void multiply() throws ExecutionException, InterruptedException {
        Calculator.Input input = Calculator.Input.newBuilder()
            .setA(2)
            .setB(3)
            .build();
        Future<Calculator.Output> future = futureStub.multiply(input);
        Calculator.Output output = future.get();
        Assertions.assertEquals(6, output.getResult());
    }
}
