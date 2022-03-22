package cn.fantasticmao.grpckit;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

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
     * The host of the server.
     */
    private String host;

    /**
     * The port of the server.
     */
    private int port;

    /**
     * The weight of the server, used in service load balancing.
     */
    private int weight = 1;

    /**
     * The tag of the server, used in service load balancing.
     */
    private String tag;

    /**
     * The application name, for backup only.
     */
    private String name;

    /**
     * The gRPC version, for backup only.
     */
    private String version;

    public static final Attributes.Key<Integer> KEY_WEIGHT = Attributes.Key.create("weight");
    public static final Attributes.Key<String> KEY_TAG = Attributes.Key.create("tag");

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
            .set(KEY_WEIGHT, this.weight)
            .set(KEY_TAG, this.tag)
            .build();
        return new EquivalentAddressGroup(address, attributes);
    }
}
