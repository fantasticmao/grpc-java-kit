package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.boot.ApplicationMetadata;
import cn.fantasticmao.grpckit.boot.ApplicationMetadataLoader;
import cn.fantasticmao.grpckit.boot.GrpcKitChannelBuilder;
import cn.fantasticmao.grpckit.boot.GrpcKitConfig;
import cn.fantasticmao.grpckit.support.ProtoUtil;
import io.grpc.Channel;
import io.grpc.ServiceDescriptor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A factory that provides names of {@code gRPC service and application},
 * and instances of {@link Channel gRPC channel}.
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
     * Cache of application names, keyed by the gRPC service name.
     */
    private final ConcurrentHashMap<String, String> appNameCache = new ConcurrentHashMap<>(64);

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

    public String getAppName(String serviceName) {
        if (appNameCache.isEmpty()) {
            synchronized (appNameCache) {
                if (appNameCache.isEmpty()) {
                    this.initAppNameCache();
                }
            }
        }
        return appNameCache.get(serviceName);
    }

    public Channel getChannel(String appName, @Nonnull GrpcKitConfig config) {
        return channelCache.computeIfAbsent(appName, key ->
            // FIXME
            GrpcKitChannelBuilder.forConfig(appName, config)
                .usePlaintext()
                .build()
        );
    }

    private void initAppNameCache() {
        List<ApplicationMetadata> metadataList = this.loadApplicationMetadata();
        for (ApplicationMetadata metadata : metadataList) {
            for (String serviceName : metadata.getServices()) {
                if (appNameCache.contains(serviceName)) {
                    throw new GrpcKitException("Duplicate gRPC service name: " + serviceName);
                } else {
                    appNameCache.put(serviceName, metadata.getName());
                }
            }
        }
    }

    private List<ApplicationMetadata> loadApplicationMetadata() {
        ServiceLoader<ApplicationMetadataLoader> serviceLoader
            = ServiceLoader.load(ApplicationMetadataLoader.class);
        return serviceLoader.stream()
            .map(ServiceLoader.Provider::get)
            .map(metadataLoader -> metadataLoader.load(null))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
