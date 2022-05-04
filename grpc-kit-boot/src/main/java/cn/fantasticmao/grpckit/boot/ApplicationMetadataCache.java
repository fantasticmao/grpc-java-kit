package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.GrpcKitException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Cache of {@link ApplicationMetadata application metadata}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-05-02
 */
public class ApplicationMetadataCache {
    /**
     * Cache of application metadata, keyed by the gRPC service name.
     */
    private final Map<String, ApplicationMetadata> cacheByServiceName = new ConcurrentHashMap<>(64);

    private static volatile ApplicationMetadataCache instance;

    private ApplicationMetadataCache() {
    }

    public static ApplicationMetadataCache getInstance() {
        if (instance == null) {
            synchronized (ApplicationMetadataCache.class) {
                if (instance == null) {
                    instance = new ApplicationMetadataCache();
                    instance.initialize();
                }
            }
        }
        return instance;
    }

    private void initialize() {
        ServiceLoader<ApplicationMetadataLoader> serviceLoader = ServiceLoader.load(ApplicationMetadataLoader.class);
        List<ApplicationMetadata> metadataList = serviceLoader.stream()
            .map(ServiceLoader.Provider::get)
            .map(metadataLoader -> metadataLoader.load(null))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        this.initializeByServiceName(metadataList);
    }

    private void initializeByServiceName(List<ApplicationMetadata> metadataList) {
        for (ApplicationMetadata metadata : metadataList) {
            for (String serviceName : metadata.getServices()) {
                if (cacheByServiceName.containsKey(serviceName)) {
                    throw new GrpcKitException("Duplicate gRPC service name: " + serviceName);
                } else {
                    cacheByServiceName.put(serviceName, metadata);
                }
            }
        }
    }

    /**
     * Get {@link ApplicationMetadata application metadata} from the cache by a specific gRPC service name.
     *
     * @param serviceName the gRPC service name.
     * @return the {@link ApplicationMetadata application metadata} instance from the cache.
     */
    public ApplicationMetadata getByServiceName(@Nonnull String serviceName) {
        return cacheByServiceName.get(serviceName);
    }
}
