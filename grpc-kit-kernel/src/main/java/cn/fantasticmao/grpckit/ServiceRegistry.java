package cn.fantasticmao.grpckit;

/**
 * Register a service instance, which implementation is independent of the gRPC, and will
 * be called when the {@link io.grpc.Server gRPC Server} has been started.
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
