package cn.fantasticmao.grpckit;

import cn.fantasticmao.grpckit.support.AttributeUtil;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * Metadata of service instances.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-14
 */
@Getter
@Setter
public class ServiceMetadata {
    /**
     * The host of a service instance.
     */
    private String host;

    /**
     * The port of a service instance.
     */
    private int port;

    /**
     * The weight of a service instance, and will be used in service load balancing.
     */
    private int weight = DEFAULT_WEIGHT;

    /**
     * The tag of a service instance, and will be used in service load balancing.
     */
    private String tag = DEFAULT_TAG;

    /**
     * The application name, for backup only.
     */
    private String name;

    /**
     * The gRPC version, for backup only.
     */
    private String version;

    public static final int DEFAULT_WEIGHT = 1;
    public static final String DEFAULT_TAG = "";

    public ServiceMetadata() {
    }

    public ServiceMetadata(String host, int port, Integer weight, String tag,
                           String name, String version) {
        this.host = host;
        this.port = port;
        this.weight = weight;
        this.tag = tag;
        this.name = name;
        this.version = version;
    }

    public EquivalentAddressGroup toAddressGroup() {
        InetSocketAddress address = new InetSocketAddress(this.host, this.port);
        Attributes attributes = Attributes.newBuilder()
            .set(AttributeUtil.KEY_WEIGHT, this.weight)
            .set(AttributeUtil.KEY_TAG, this.tag)
            .build();
        return new EquivalentAddressGroup(address, attributes);
    }
}
