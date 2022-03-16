package cn.fantasticmao.grpckit;

/**
 * Service metadata
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-14
 */
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

    // getter and setter

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
