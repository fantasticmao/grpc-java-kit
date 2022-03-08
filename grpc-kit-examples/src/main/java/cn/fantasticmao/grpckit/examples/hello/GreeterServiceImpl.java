package cn.fantasticmao.grpckit.examples.hello;

import cn.fantasticmao.grpckit.examples.proto.GreeterServiceGrpc;
import cn.fantasticmao.grpckit.examples.proto.HelloRequest;
import cn.fantasticmao.grpckit.examples.proto.HelloResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GreeterServiceImpl
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-07-31
 */
public class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreeterServiceImpl.class);

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        LOGGER.info("Server *** receive a new message, name: {}", request.getName());

        HelloResponse response = HelloResponse.newBuilder()
            .setMessage("Hello " + request.getName())
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
