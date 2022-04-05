package cn.fantasticmao.grpckit.proto;

import cn.fantasticmao.grpckit.GrpcKitFactory;
import io.grpc.BindableService;
import io.grpc.Channel;
import io.grpc.Server;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GreeterServiceTest
 * <p>
 * Start a ZooKeeper container for testing: {@code docker run -d -p 2181:2181 --rm --name zookeeper-test zookeeper:3.7.0}
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-07-31
 */
@Disabled
public class GreeterServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreeterServiceTest.class);

    @Test
    public void server_1() throws InterruptedException {
        final BindableService service = new GreeterService();
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-server-1.yaml");
        final Server server = factory.newAndStartServer(service);
        server.awaitTermination();
    }

    @Test
    public void server_2() throws InterruptedException {
        final BindableService service = new GreeterService();
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-server-2.yaml");
        final Server server = factory.newAndStartServer(service);
        server.awaitTermination();
    }

    @Test
    public void stub() {
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-stub.yaml");
        final Channel channel = factory.newChannel("unit_test_server");
        final GreeterServiceGrpc.GreeterServiceBlockingStub stub = factory.newStub(
            GreeterServiceGrpc.GreeterServiceBlockingStub.class, channel);

        HelloRequest request = HelloRequest.newBuilder()
            .setName("fantasticmao")
            .build();
        for (int i = 0; i < 10; i++) {
            LOGGER.info("Client *** greeting, name: {}", request.getName());
            HelloResponse response = stub.sayHello(request);
            LOGGER.info("Client *** receive a new message: {}", response.getMessage());
        }
    }
}
