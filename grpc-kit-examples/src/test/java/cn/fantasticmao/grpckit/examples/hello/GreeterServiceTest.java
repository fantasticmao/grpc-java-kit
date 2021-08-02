package cn.fantasticmao.grpckit.examples.hello;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * GreeterServiceTest
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021-07-31
 */
public class GreeterServiceTest {

    @Test
    public void greeting() throws IOException {
        final String host = "localhost";
        final int port = 50051;

        Server server = ServerBuilder
            .forPort(port)
            .addService(new GreeterServiceImpl())
            .build();
        server.start();
        System.out.println("Server *** started, listening on " + port);

        try {
            ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
            GreeterServiceGrpc.GreeterServiceBlockingStub stub = GreeterServiceGrpc.newBlockingStub(channel);
            GreeterProto.HelloRequest request = GreeterProto.HelloRequest.newBuilder()
                .setName("fantasticmao")
                .build();
            System.out.println("Client *** greeting, name: " + request.getName());
            GreeterProto.HelloResponse response = stub.sayHello(request);
            System.out.println("Client *** receive a new message: " + response.getMessage());
        } finally {
            server.shutdown();
            System.out.println("Server *** terminated");
        }
    }
}