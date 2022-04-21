package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.proto.Calculator;
import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcService;
import io.grpc.stub.StreamObserver;

/**
 * CalculatorService
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-04
 */
@GrpcService
public class CalculatorService extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void plus(Calculator.Input input, StreamObserver<Calculator.Output> responseObserver) {
        Calculator.Output output = Calculator.Output.newBuilder()
            .setResult(input.getA() + input.getB())
            .build();
        responseObserver.onNext(output);
        responseObserver.onCompleted();
    }

    @Override
    public void minus(Calculator.Input input, StreamObserver<Calculator.Output> responseObserver) {
        Calculator.Output output = Calculator.Output.newBuilder()
            .setResult(input.getA() - input.getB())
            .build();
        responseObserver.onNext(output);
        responseObserver.onCompleted();
    }
}
