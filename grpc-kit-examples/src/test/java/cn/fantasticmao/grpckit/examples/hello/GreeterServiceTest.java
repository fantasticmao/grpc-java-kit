package cn.fantasticmao.grpckit.examples.hello;

import cn.fantasticmao.grpckit.*;
import cn.fantasticmao.grpckit.examples.proto.GreeterServiceGrpc;
import cn.fantasticmao.grpckit.examples.proto.HelloRequest;
import cn.fantasticmao.grpckit.examples.proto.HelloResponse;
import cn.fantasticmao.grpckit.support.NetUtil;
import cn.fantasticmao.grpckit.support.UriUtil;
import io.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * GreeterServiceTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-07-31
 */
@Disabled
public class GreeterServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreeterServiceTest.class);

    @Test
    public void server() throws IOException, InterruptedException {
        final BindableService service = new GreeterServiceImpl();

        final String serviceName = ServiceBuddy.getServiceName(service);
        final String serviceGroup = GrpcKitConfig.getInstance().getGrpc().getGroup();
        final String registryUri = GrpcKitConfig.getInstance().getGrpc().getRegistryUri();
        Assertions.assertNotNull(registryUri);

        final InetAddress address;
        try {
            address = NetUtil.getLocalAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new GrpcKitException("Get local address error", e);
        }

        final int port = GrpcKitConfig.getInstance().getGrpc().getServer().getPort();
        URI serviceUri = UriUtil.newServiceUri(URI.create(registryUri), serviceName, serviceGroup, address, port);

        Server server = ServerBuilder
            .forPort(port)
            .addService(service)
            .build();
        server.start();

        GrpcKitConfig.Grpc.Server serverConf = GrpcKitConfig.getInstance().getGrpc().getServer();
        ServiceMetadata metadata = new ServiceMetadata(serverConf.getWeight(), serverConf.getTag(), Constant.VERSION);
        ServiceBuddy.registerService(serviceUri, metadata);
        LOGGER.info("Server *** started, listening on {}", port);

        server.awaitTermination();
    }

    @Test
    public void client() {
        final String serviceName = GreeterServiceGrpc.SERVICE_NAME;
        final String serviceGroup = GrpcKitConfig.getInstance().getGrpc().getGroup();
        final String registryUri = GrpcKitConfig.getInstance().getGrpc().getRegistryUri();
        Assertions.assertNotNull(registryUri);

        URI serviceUri = UriUtil.newServiceUri(URI.create(registryUri), serviceName, serviceGroup);
        final int timeout = GrpcKitConfig.getInstance().getGrpc().getClient().getTimeoutMs();

        ManagedChannel channel = ManagedChannelBuilder
            .forTarget(serviceUri.toString())
            .usePlaintext()
            .build();
        GreeterServiceGrpc.GreeterServiceBlockingStub stub = GreeterServiceGrpc.newBlockingStub(channel)
            .withDeadlineAfter(timeout, TimeUnit.MILLISECONDS);
        HelloRequest request = HelloRequest.newBuilder()
            .setName("fantasticmao")
            .build();
        LOGGER.info("Client *** greeting, name: {}", request.getName());
        HelloResponse response = stub.sayHello(request);
        LOGGER.info("Client *** receive a new message: {}", response.getMessage());
    }
}
