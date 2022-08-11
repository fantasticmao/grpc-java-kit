package cn.fantasticmao.grpckit.proto;

import cn.fantasticmao.grpckit.boot.GrpcKitChannelBuilder;
import cn.fantasticmao.grpckit.boot.GrpcKitServerBuilder;
import cn.fantasticmao.grpckit.boot.GrpcKitStubFactory;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfigLoader;
import cn.fantasticmao.grpckit.boot.factory.GrpcKitChannelBuilderFactory;
import cn.fantasticmao.grpckit.boot.factory.GrpcKitServerBuilderFactory;
import io.grpc.Channel;
import io.grpc.Server;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ExampleDnsTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-11
 */
public class ExampleDnsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleDnsTest.class);

    @Test
    public void example() throws IOException {
        final String appName = "localhost:50051";

        // build servers
        final GrpcKitConfig serverConfig = GrpcKitConfigLoader.YAML.loadAndParse("grpc-kit-dns-server.yml");

        final Server server = GrpcKitServerBuilder.forConfig(appName, serverConfig)
            .customize(GrpcKitServerBuilderFactory.Default.INSTANCE)
            .addService(new GreeterService("dns"))
            .build();

        // start servers
        server.start();

        try {
            // new channel and stub
            final GrpcKitConfig clientConfig = GrpcKitConfigLoader.YAML.loadAndParse("grpc-kit-dns-client.yml");
            final Channel channel = GrpcKitChannelBuilder.forConfig(appName, appName, clientConfig)
                .customize(GrpcKitChannelBuilderFactory.Default.INSTANCE)
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
            server.shutdown();
        }
    }
}
