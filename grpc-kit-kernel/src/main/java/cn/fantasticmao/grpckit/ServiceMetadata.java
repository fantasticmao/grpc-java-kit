package cn.fantasticmao.grpckit;

import cn.fantasticmao.grpckit.support.AttributeUtil;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
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
     * Host of a service instance.
     */
    private String host;

    /**
     * Port of a service instance.
     */
    private int port;

    /**
     * Weight of a service instance, will be used in service load balancing.
     */
    private int weight = DEFAULT_WEIGHT;

    /**
     * Tag of a service instance, will be used in service load balancing.
     */
    private String tag = DEFAULT_TAG;

    /**
     * Version of gRPC, for backup only.
     */
    private String version;

    /**
     * The app name of the service
     */
    private String appName;

    public static final int DEFAULT_WEIGHT = 1;

    public static final String DEFAULT_TAG = "";

    public ServiceMetadata() {
    }

    public ServiceMetadata(InetAddress address, int port, Integer weight, String tag,
                           String version, String appName) {
        this.host = address.getHostAddress();
        this.port = port;
        this.weight = weight;
        this.tag = tag;
        this.version = version;
        this.appName = appName;
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
