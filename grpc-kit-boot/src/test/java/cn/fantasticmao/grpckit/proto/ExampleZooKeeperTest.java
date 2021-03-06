package cn.fantasticmao.grpckit.proto;

import cn.fantasticmao.grpckit.boot.GrpcKitChannelBuilder;
import cn.fantasticmao.grpckit.boot.GrpcKitServerBuilder;
import cn.fantasticmao.grpckit.boot.GrpcKitStubFactory;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfigLoader;
import io.grpc.Channel;
import io.grpc.Server;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * GreeterServiceTest
 * <p>
 * Start a ZooKeeper container for testing: {@code docker run -d -p 2181:2181 --rm --name zookeeper-test zookeeper:3.7.0}
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-07-31
 */
public class ExampleZooKeeperTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleZooKeeperTest.class);

    @Test
    public void example() throws IOException {
        final String appName = "unit_test";

        // build servers
        final GrpcKitConfig serverConfig_1 = GrpcKitConfigLoader.YAML.loadAndParse("grpc-kit-zookeeper-server-1.yml");
        final GrpcKitConfig serverConfig_2 = GrpcKitConfigLoader.YAML.loadAndParse("grpc-kit-zookeeper-server-2.yml");

        final Server server_1 = GrpcKitServerBuilder.forConfig(appName, serverConfig_1)
            .addService(new GreeterService("zookeeper_1"))
            .addService(ProtoReflectionService.newInstance())
            .build();
        final Server server_2 = GrpcKitServerBuilder.forConfig(appName, serverConfig_2)
            .addService(new GreeterService("zookeeper_2"))
            .addService(ProtoReflectionService.newInstance())
            .build();

        // start servers
        server_1.start();
        server_2.start();

        try {
            // new channel and stub
            final GrpcKitConfig clientConfig = GrpcKitConfigLoader.YAML.loadAndParse("grpc-kit-zookeeper-client.yml");
            final Channel channel = GrpcKitChannelBuilder.forConfig(appName, clientConfig)
                .usePlaintext()
                .build();
            final GreeterServiceGrpc.GreeterServiceBlockingStub stub = GrpcKitStubFactory.newStub(
                GreeterServiceGrpc.GreeterServiceBlockingStub.class, channel, clientConfig);

            // send requests
            HelloRequest request = HelloRequest.newBuilder()
                .setName("fantasticmao")
                .build();
            for (int i = 0; i < 10; i++) {
                LOGGER.info("[Stub] greeting, name: {}", request.getName());
                HelloResponse response = stub.sayHello(request);
                LOGGER.info("[Stub] receive a new message: {}", response.getMessage());
            }
        } finally {
            // shutdown servers
            server_1.shutdown();
            server_2.shutdown();
        }
    }
}
