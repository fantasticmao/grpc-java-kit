package cn.fantasticmao.grpckit.examples.hello;

import io.grpc.stub.StreamObserver;

/**
 * GreeterServiceImpl
 *
 * @author maomao
 * @since 2021-07-31
 */
public class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

    @Override
    public void sayHello(GreeterProto.HelloRequest request, StreamObserver<GreeterProto.HelloResponse> responseObserver) {
        System.out.println("Server *** receive a new message, name: " + request.getName());

        GreeterProto.HelloResponse response = GreeterProto.HelloResponse.newBuilder()
            .setMessage("Hello " + request.getName())
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
