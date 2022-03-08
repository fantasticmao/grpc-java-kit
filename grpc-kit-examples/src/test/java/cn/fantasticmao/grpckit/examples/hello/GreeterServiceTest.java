package cn.fantasticmao.grpckit.examples.hello;

import cn.fantasticmao.grpckit.examples.proto.GreeterServiceGrpc;
import cn.fantasticmao.grpckit.examples.proto.HelloRequest;
import cn.fantasticmao.grpckit.examples.proto.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * GreeterServiceTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-07-31
 */
public class GreeterServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreeterServiceTest.class);

    @Test
    public void sayHello() throws IOException {
        final String host = "localhost";
        final int port = 50051;

        Server server = ServerBuilder
            .forPort(port)
            .addService(new GreeterServiceImpl())
            .build();
        server.start();
        LOGGER.info("Server *** started, listening on {}", port);

        try {
            ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
            GreeterServiceGrpc.GreeterServiceBlockingStub stub = GreeterServiceGrpc.newBlockingStub(channel);
            HelloRequest request = HelloRequest.newBuilder()
                .setName("fantasticmao")
                .build();
            LOGGER.info("Client *** greeting, name: {}", request.getName());
            HelloResponse response = stub.sayHello(request);
            LOGGER.info("Client *** receive a new message: {}", response.getMessage());
        } finally {
            server.shutdown();
            LOGGER.info("Server *** terminated");
        }
    }
}
