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
    public void server_1() throws IOException, InterruptedException {
        System.setProperty("cn.fantasticmao.grpckit.config", "grpc-kit-server-1.yaml");

        final String appName = GrpcKitConfig.getInstance().getName();
        Assertions.assertNotNull(appName);

        final BindableService service = new GreeterServiceImpl();

        final String serviceName = ServiceBuddy.getServiceName(service);
        final String serviceGroup = GrpcKitConfig.getInstance().getGrpc().getGroup();

        final String registry = GrpcKitConfig.getInstance().getNameResolver().getRegistry();
        Assertions.assertNotNull(registry);

        final InetAddress address;
        try {
            address = NetUtil.getLocalAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new GrpcKitException("Get local address error", e);
        }

        final int port = GrpcKitConfig.getInstance().getGrpc().getServer().getPort();
        URI serviceUri = UriUtil.newServiceUri(URI.create(registry), serviceName, serviceGroup, address, port);

        Server server = ServerBuilder
            .forPort(port)
            .addService(service)
            .build();
        server.start();

        GrpcKitConfig.Grpc.Server serverConf = GrpcKitConfig.getInstance().getGrpc().getServer();
        ServiceMetadata metadata = new ServiceMetadata(address.getHostAddress(), port, serverConf.getWeight(),
            serverConf.getTag(), appName, Constant.VERSION);
        ServiceBuddy.registerService(serviceUri, metadata);
        LOGGER.info("Server *** started, listening on {}", port);

        server.awaitTermination();
    }

    @Test
    public void server_2() throws IOException, InterruptedException {
        System.setProperty("cn.fantasticmao.grpckit.config", "grpc-kit-server-2.yaml");

        final String appName = GrpcKitConfig.getInstance().getName();
        Assertions.assertNotNull(appName);

        final BindableService service = new GreeterServiceImpl();

        final String serviceName = ServiceBuddy.getServiceName(service);
        final String serviceGroup = GrpcKitConfig.getInstance().getGrpc().getGroup();

        final String registry = GrpcKitConfig.getInstance().getNameResolver().getRegistry();
        Assertions.assertNotNull(registry);

        final InetAddress address;
        try {
            address = NetUtil.getLocalAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new GrpcKitException("Get local address error", e);
        }

        final int port = GrpcKitConfig.getInstance().getGrpc().getServer().getPort();
        URI serviceUri = UriUtil.newServiceUri(URI.create(registry), serviceName, serviceGroup, address, port);

        Server server = ServerBuilder
            .forPort(port)
            .addService(service)
            .build();
        server.start();

        GrpcKitConfig.Grpc.Server serverConf = GrpcKitConfig.getInstance().getGrpc().getServer();
        ServiceMetadata metadata = new ServiceMetadata(address.getHostAddress(), port, serverConf.getWeight(),
            serverConf.getTag(), appName, Constant.VERSION);
        ServiceBuddy.registerService(serviceUri, metadata);
        LOGGER.info("Server *** started, listening on {}", port);

        server.awaitTermination();
    }

    @Test
    public void client() {
        System.setProperty("cn.fantasticmao.grpckit.config", "grpc-kit-client.yaml");

        final String appName = GrpcKitConfig.getInstance().getName();
        Assertions.assertNotNull(appName);

        final String serviceName = GreeterServiceGrpc.SERVICE_NAME;
        final String serviceGroup = GrpcKitConfig.getInstance().getGrpc().getGroup();

        final String registry = GrpcKitConfig.getInstance().getNameResolver().getRegistry();
        Assertions.assertNotNull(registry);

        URI serviceUri = UriUtil.newServiceUri(URI.create(registry), serviceName, serviceGroup);
        final int timeout = GrpcKitConfig.getInstance().getGrpc().getClient().getTimeout();

        ManagedChannel channel = ManagedChannelBuilder
            .forTarget(serviceUri.toString())
            .userAgent(appName)
            .defaultLoadBalancingPolicy(ServiceLoadBalancer.Policy.WEIGHTED_RANDOM.name)
            .usePlaintext()
            .build();
        GreeterServiceGrpc.GreeterServiceBlockingStub stub = GreeterServiceGrpc.newBlockingStub(channel)
            .withOption(Constant.KEY_OPTION_TAG, "debug")
            .withDeadlineAfter(timeout, TimeUnit.MILLISECONDS);
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
