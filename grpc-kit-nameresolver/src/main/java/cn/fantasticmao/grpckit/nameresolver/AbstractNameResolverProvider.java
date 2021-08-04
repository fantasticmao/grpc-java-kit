package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolverProvider;

/**
 * AbstractNameResolverProvider
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021-08-03
 */
public abstract class AbstractNameResolverProvider extends NameResolverProvider {

    /**
     * Allow using a JVM option to disable this provider
     *
     * @return JVM property key
     */
    protected abstract String getVmOptionKey();

    @Override
    protected boolean isAvailable() {
        String vmOptionKey = this.getVmOptionKey();
        // available is the default.
        String vmOptionalVal = System.getProperty(vmOptionKey, Boolean.TRUE.toString());
        return Boolean.parseBoolean(vmOptionalVal);
    }

    @Override
    protected int priority() {
        // bigger than the default value
        return 5 + 1;
    }
}
