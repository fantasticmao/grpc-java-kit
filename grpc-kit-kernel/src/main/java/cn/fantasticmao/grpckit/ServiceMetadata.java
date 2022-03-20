package cn.fantasticmao.grpckit;

import lombok.Getter;
import lombok.Setter;

/**
 * The metadata of service instance.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-14
 */
@Getter
@Setter
public class ServiceMetadata {
    /**
     * The weight of the server, used in service load balancing.
     */
    private int weight = 1;

    /**
     * The tag of the server, used in service load balancing.
     */
    private String tag;

    /**
     * The gRPC version, for backup only.
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
