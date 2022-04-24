package cn.fantasticmao.grpckit.support;

import cn.fantasticmao.grpckit.proto.GreeterServiceGrpc;
import io.grpc.ServiceDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ProtoUtilTest
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
public class ProtoUtilTest {

    @Test
    public void getServiceDescriptor() {
        ServiceDescriptor serviceDescriptor = ProtoUtil.getServiceDescriptor(
            GreeterServiceGrpc.GreeterServiceBlockingStub.class);
        String serviceName = serviceDescriptor.getName();
        Assertions.assertEquals(GreeterServiceGrpc.SERVICE_NAME, serviceName);
    }

    @Test
    public void getGrpcName() {
        String grpcName = ProtoUtil.getGrpcName(GreeterServiceGrpc.GreeterServiceBlockingStub.class);
        Assertions.assertEquals(GreeterServiceGrpc.class.getName(), grpcName);
    }

    @Test
    public void getStubSuffix() {
        Assertions.assertEquals("Stub", ProtoUtil.getStubSuffix(
            GreeterServiceGrpc.GreeterServiceStub.class));
        Assertions.assertEquals("BlockingStub", ProtoUtil.getStubSuffix(
            GreeterServiceGrpc.GreeterServiceBlockingStub.class));
        Assertions.assertEquals("FutureStub", ProtoUtil.getStubSuffix(
            GreeterServiceGrpc.GreeterServiceFutureStub.class));
    }
}
