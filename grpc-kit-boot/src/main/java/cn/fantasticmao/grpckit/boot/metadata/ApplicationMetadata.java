package cn.fantasticmao.grpckit.boot.metadata;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Application metadata provided by gRPC services for gRPC clients.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
@Getter
@Setter
public final class ApplicationMetadata {
    /**
     * Name of the application.
     */
    private String name;

    /**
     * All service names registered in the gRPC server.
     */
    private List<String> services;

}
