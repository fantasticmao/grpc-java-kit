package cn.fantasticmao.grpckit;

import lombok.Getter;
import lombok.Setter;

/**
 * Service metadata.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-14
 */
@Getter
@Setter
public class ServiceMetadata {
    /**
     * Weights for selecting services in load balancing
     */
    private Integer weight;

    /**
     * Tags for selecting services in load balancing
     */
    private String tag;

    /**
     * Back up the gRPC version
     */
    private String version;

    public ServiceMetadata() {
    }

    public ServiceMetadata(Integer weight, String tag, String version) {
        this.weight = weight;
        this.tag = tag;
        this.version = version;
    }
}
