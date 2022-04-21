package cn.fantasticmao.grpckit.proto;

import cn.fantasticmao.grpckit.GrpcKitFactory;
import io.grpc.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GreeterServiceTest
 * <p>
 * <h3>Start a ZooKeeper container for testing method {@link GreeterServiceTest#server_1} & {@link GreeterServiceTest#server_2} & {@link GreeterServiceTest#client_1}:</h3>
 * {@code docker run -d -p 2181:2181 --rm --name zookeeper-test zookeeper:3.7.0}
 * <p>
 * <h3>Start a nacos container for testing method {@link GreeterServiceTest#server_3} & {@link GreeterServiceTest#server_4} & {@link GreeterServiceTest#client_2}:</h3>
 * {@code docker run --name nacos -e MODE=standalone -p 8848:8848 -p 9848:9848 -p 9849:9849 -d nacos/nacos-server:2.0.2}
 * <p>
 * @author fantasticmao
 * @author harrison
 * @version 1.39.0
 * @since 2021-07-31
 */
@Disabled
public class GreeterServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreeterServiceTest.class);

    /**
     * zk 作为注册中心
     * @throws InterruptedException
     */
    @Test
    public void server_1() throws InterruptedException {
        final BindableService service = new GreeterService();
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-server-1.yaml");
        final Server server = factory.newAndStartServer(service);
        server.awaitTermination();
    }

    /**
     * zk 作为注册中心
     * @throws InterruptedException
     */
    @Test
    public void server_2() throws InterruptedException {
        final BindableService service = new GreeterService();
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-server-2.yaml");
        final Server server = factory.newAndStartServer(service);
        server.awaitTermination();
    }

    /**
     * zk 作为注册中心
     * @throws InterruptedException
     */
    @Test
    public void client_1() {
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-client-1.yaml");
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

    /**
     * nacos 作为注册中心
     * @throws InterruptedException
     */
    @Test
    public void server_3() throws InterruptedException {
        final BindableService service = new GreeterService();
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-server-3.yaml");
        final Server server = factory.newAndStartServer(service);
        server.awaitTermination();
    }

    /**
     * nacos 作为注册中心
     * @throws InterruptedException
     */
    @Test
    public void server_4() throws InterruptedException {
        final BindableService service = new GreeterService();
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-server-4.yaml");
        final Server server = factory.newAndStartServer(service);
        server.awaitTermination();
    }

    /**
     * nacos 作为注册中心
     */
    @Test
    public void client_2() {
        final GrpcKitFactory factory = new GrpcKitFactory("grpc-kit-client-2.yaml");
        final Channel channel = factory.newChannel("unit_test_server");
        final GreeterServiceGrpc.GreeterServiceBlockingStub stub = factory.newStub(
            GreeterServiceGrpc.GreeterServiceBlockingStub.class, channel);

        HelloRequest request = HelloRequest.newBuilder()
            .setName("harrison")
            .build();
        for (int i = 0; i < 10; i++) {
            LOGGER.info("Client *** greeting, name: {}", request.getName());
            HelloResponse response = stub.sayHello(request);
            LOGGER.info("Client *** receive a new message: {}", response.getMessage());
        }
    }
}
