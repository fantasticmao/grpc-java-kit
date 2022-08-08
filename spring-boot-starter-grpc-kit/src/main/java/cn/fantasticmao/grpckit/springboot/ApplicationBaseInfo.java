package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.boot.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.springboot.factory.GrpcKitChannelBuilderFactory;
import cn.fantasticmao.grpckit.springboot.factory.GrpcKitServerBuilderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * ApplicationBaseInfo
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-08-08
 */
public abstract class ApplicationBaseInfo implements ApplicationContextAware {
    @Nullable
    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * By default, if no application name is set, "application" will be used.
     *
     * @see org.springframework.boot.context.ContextIdApplicationContextInitializer
     */
    protected String getCurrentAppName() {
        Objects.requireNonNull(applicationContext, "applicationContext must not be null");
        return applicationContext.getId();
    }

    /**
     * Get the {@link GrpcKitConfig} from Spring.
     */
    @Nonnull
    protected GrpcKitConfig getGrpcKitConfig() {
        Objects.requireNonNull(applicationContext, "applicationContext must not be null");
        return applicationContext.getBean(GrpcKitConfig.class).validate();
    }

    /**
     * Get the {@link GrpcKitServerBuilderFactory} from Spring, if not present,
     * use the {@link GrpcKitServerBuilderFactory.Default default factory}.
     *
     * @return factory used to build gRPC server.
     */
    protected GrpcKitServerBuilderFactory getGrpcKitServerBuilderFactory() {
        Objects.requireNonNull(applicationContext, "applicationContext must not be null");
        try {
            return applicationContext.getBean(GrpcKitServerBuilderFactory.class);
        } catch (NoSuchBeanDefinitionException e) {
            return GrpcKitServerBuilderFactory.Default.INSTANCE;
        }
    }

    /**
     * Get the {@link GrpcKitChannelBuilderFactory} from Spring, if not present,
     * use the {@link GrpcKitChannelBuilderFactory.Default default factory}.
     *
     * @return factory used to build gRPC channel.
     */
    protected GrpcKitChannelBuilderFactory getGrpcKitChannelBuilderFactory() {
        Objects.requireNonNull(applicationContext, "applicationContext must not be null");
        try {
            return applicationContext.getBean(GrpcKitChannelBuilderFactory.class);
        } catch (NoSuchBeanDefinitionException e) {
            return GrpcKitChannelBuilderFactory.Default.INSTANCE;
        }
    }

}
