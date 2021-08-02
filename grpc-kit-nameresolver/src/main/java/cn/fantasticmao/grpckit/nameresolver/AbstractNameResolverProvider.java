package cn.fantasticmao.grpckit.nameresolver;

import io.grpc.NameResolverProvider;

/**
 * AbstractNameResolverProvider
 *
 * @author maomao
 * @since 2021-08-03
 */
public abstract class AbstractNameResolverProvider extends NameResolverProvider {

    /**
     * Allow using a JVM property to disable this provider
     *
     * @return JVM property key
     */
    protected abstract String getVmSwitchKey();

    @Override
    protected boolean isAvailable() {
        String vmSwitchKey = this.getVmSwitchKey();
        // available is the default.
        String vmSwitchProperty = System.getProperty(vmSwitchKey, Boolean.TRUE.toString());
        return Boolean.parseBoolean(vmSwitchProperty);
    }

    @Override
    protected int priority() {
        // bigger than the default value
        return 5 + 1;
    }
}
