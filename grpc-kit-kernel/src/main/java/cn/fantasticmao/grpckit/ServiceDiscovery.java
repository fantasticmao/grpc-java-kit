package cn.fantasticmao.grpckit;

import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Discover available service instances, implemented by using gRPC {@link io.grpc.NameResolver}
 * and {@link io.grpc.NameResolverProvider} plugins.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.internal.DnsNameResolver
 * @since 2022-03-13
 */
public abstract class ServiceDiscovery extends NameResolver {
    @Nullable
    protected final Executor executor;

    public ServiceDiscovery(@Nullable Executor executor) {
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     */
    public abstract String getServiceAuthority();

    /**
     * {@inheritDoc}
     */
    public void start(Listener2 listener) {
        Future<List<ServiceMetadata>> future = this.executor != null
            ? CompletableFuture.supplyAsync(this::lookup, this.executor)
            : CompletableFuture.supplyAsync(this::lookup);
        int timeout = GrpcKitConfig.getInstance().getNameResolver().getTimeout();
        final List<ServiceMetadata> serviceMetadataList;
        try {
            serviceMetadataList = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new GrpcKitException("Service Discovery error", e);
        }

        List<EquivalentAddressGroup> servers = serviceMetadataList.stream()
            .map(metadata -> new EquivalentAddressGroup(metadata.toAddress(), metadata.toAttributes()))
            .collect(Collectors.toList());
        ResolutionResult result = ResolutionResult.newBuilder()
            .setAddresses(servers)
            .build();
        listener.onResult(result);
    }

    /**
     * Lookup available service instances and return list of service metadata.
     */
    public abstract List<ServiceMetadata> lookup();

    /**
     * {@inheritDoc}
     */
    public abstract void shutdown();

}
