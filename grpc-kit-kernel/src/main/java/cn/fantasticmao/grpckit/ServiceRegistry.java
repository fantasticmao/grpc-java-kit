package cn.fantasticmao.grpckit;

/**
 * Register a service instance, implemented independent of the gRPC, and will
 * be called by {@link GrpcKitFactory} when {@link io.grpc.Server gRPC Server} started.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public abstract class ServiceRegistry {

    /**
     * Register service instances.
     *
     * @param metadata service metadata
     * @return if succeed
     */
    public abstract boolean doRegister(ServiceMetadata metadata);

    public abstract void shutdown();

}
