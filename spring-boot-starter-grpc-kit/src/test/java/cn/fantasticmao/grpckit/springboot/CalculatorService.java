package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.proto.CalculatorServiceGrpc;
import cn.fantasticmao.grpckit.proto.Input;
import cn.fantasticmao.grpckit.proto.Output;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcService;
import io.grpc.stub.StreamObserver;

/**
 * CalculatorService
 *
 * @author fantasticmao
 * @since 2022-04-04
 */
@GrpcService
public class CalculatorService extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void add(Input input, StreamObserver<Output> responseObserver) {
        Output output = Output.newBuilder()
            .setResult(input.getA() + input.getB())
            .build();
        responseObserver.onNext(output);
        responseObserver.onCompleted();
    }
}
