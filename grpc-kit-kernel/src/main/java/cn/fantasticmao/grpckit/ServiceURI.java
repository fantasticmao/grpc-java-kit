package cn.fantasticmao.grpckit;

import java.net.URI;

/**
 * A URI associated with registryUri as well as application name and group, which will be
 * used to identify a gRPC service in service discovery and registration.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-06
 */
public abstract class ServiceURI {
    protected final URI registryUri;
    protected final String appName;
    protected final String appGroup;

    protected ServiceURI(URI registryUri, String appName, String appGroup) {
        this.registryUri = registryUri;
        this.appName = appName;
        this.appGroup = appGroup;
    }

    public abstract URI toTargetUri();
}
