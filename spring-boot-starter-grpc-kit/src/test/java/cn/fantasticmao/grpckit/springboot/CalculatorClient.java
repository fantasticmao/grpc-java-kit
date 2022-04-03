package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * GreeterClient
 *
 * @author fantasticmao
 * @since 2022-04-04
 */
@Service
public class CalculatorClient {
    @GrpcClient
    private CalculatorServiceGrpc.CalculatorServiceBlockingStub blockingStub;

    public int add(int a, int b) {
        //Input input = Input.newBuilder()
        //    .setA(a)
        //    .setB(b)
        //    .build();
        //Output output = blockingStub.add(input);
        //return output.getResult();
        return a + b;
    }
}
