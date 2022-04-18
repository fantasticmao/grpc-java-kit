package cn.fantasticmao.grpckit.springboot;

import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * Event published by a {@link GrpcServerStartApplicationListener} when gRPC
 * {@link cn.fantasticmao.grpckit.GrpcKitConfig.Grpc.Server Server} has been started.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-13
 */
public class GrpcServerStartedEvent extends ApplicationEvent {
    private final Set<String> serviceNames;

    public GrpcServerStartedEvent(Object source, Set<String> serviceNames) {
        super(source);
        this.serviceNames = serviceNames;
    }

}