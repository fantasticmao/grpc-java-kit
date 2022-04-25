package cn.fantasticmao.grpckit.springboot;

import io.grpc.Channel;
import io.grpc.stub.AbstractStub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GrpcStubBeanFactory
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
public class GrpcStubBeanFactory {
    /**
     * Map of gRPC service names, keyed by the gRPC stub class.
     */
    private final Map<Class<? extends AbstractStub<?>>, String> serviceNameMap = new ConcurrentHashMap<>(256);

    /**
     * Map of application names, keyed by the gRPC service name.
     */
    private final Map<String, String> applicationMap = new ConcurrentHashMap<>(128);

    /**
     * Map of gRPC {@link io.grpc.Channel Channel}s, keyed by the application name.
     */
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>(64);
}
