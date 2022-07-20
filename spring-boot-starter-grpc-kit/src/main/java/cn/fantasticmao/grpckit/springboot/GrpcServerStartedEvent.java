package cn.fantasticmao.grpckit.springboot;

import io.grpc.Server;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.List;

/**
 * Event published by a {@link GrpcServerContainer} when gRPC {@link io.grpc.Server Server}
 * has been started.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-13
 */
public class GrpcServerStartedEvent extends ApplicationEvent {
    private final List<String> serviceNames;

    public GrpcServerStartedEvent(Server server, List<String> serviceNames) {
        super(server);
        this.serviceNames = Collections.unmodifiableList(serviceNames);
    }

    public List<String> getServiceNames() {
        return serviceNames;
    }
}
