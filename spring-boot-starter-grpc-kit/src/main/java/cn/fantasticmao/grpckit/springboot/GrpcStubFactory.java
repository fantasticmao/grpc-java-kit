package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.boot.GrpcKitChannelBuilder;
import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import cn.fantasticmao.grpckit.support.ProtoUtil;
import io.grpc.Channel;
import io.grpc.ServiceDescriptor;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A factory for gRPC {@link io.grpc.stub.AbstractStub Stub}s.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
public class GrpcStubFactory {
    /**
     * Cache of gRPC service names, keyed by the gRPC stub class.
     */
    private final ConcurrentHashMap<Class<?>, String> serviceNameCache = new ConcurrentHashMap<>(128);

    /**
     * Map of application names, keyed by the gRPC service name.
     */
    private final Map<String, String> applicationMap = new ConcurrentHashMap<>(64);

    /**
     * Cache of gRPC {@link io.grpc.Channel Channel}s, keyed by the application name.
     */
    private final ConcurrentHashMap<String, Channel> channelCache = new ConcurrentHashMap<>(64);

    public GrpcStubFactory() {
    }

    public String getServiceName(Class<?> stubClass) {
        return serviceNameCache.computeIfAbsent(stubClass, key -> {
                ServiceDescriptor serviceDescriptor = ProtoUtil.getServiceDescriptor(stubClass);
                return serviceDescriptor.getName();
            }
        );
    }

    public Channel getChannel(String appName, @Nonnull GrpcKitConfig config) {
        return channelCache.computeIfAbsent(appName, key ->
            GrpcKitChannelBuilder.forConfig(appName, config)
                .usePlaintext()
                .build()
        );
    }
}
