package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.ServiceLoadBalancer;
import cn.fantasticmao.grpckit.ServiceURI;
import cn.fantasticmao.grpckit.boot.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.boot.factory.GrpcKitChannelBuilderFactory;
import cn.fantasticmao.grpckit.boot.metadata.ApplicationNameValidator;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.AbstractManagedChannelImplBuilder;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.concurrent.Executor;

/**
 * A builder for creating {@link ManagedChannel gRPC ManagedChannel} instances.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-21
 */
public class GrpcKitChannelBuilder extends AbstractManagedChannelImplBuilder<GrpcKitChannelBuilder> {
    private final String srcAppName;
    private final String dstAppName;
    private final GrpcKitConfig config;
    private final ManagedChannelBuilder<?> managedChannelBuilder;

    private GrpcKitChannelBuilder(String srcAppName, String dstAppName, @Nonnull GrpcKitConfig config) {
        this.srcAppName = srcAppName;
        this.dstAppName = dstAppName;
        this.config = config;

        final String appGroup = config.getGroup();
        final String registry = config.getNameResolver().getRegistry();
        final String policy = config.getLoadBalancer().getPolicy();
        final ServiceURI serviceUri = ServiceURI.Factory.loadWith(URI.create(registry), dstAppName, appGroup);
        this.managedChannelBuilder = ManagedChannelBuilder.forTarget(serviceUri.toTargetUri().toString())
            .userAgent(srcAppName)
            .defaultLoadBalancingPolicy(ServiceLoadBalancer.Policy.of(policy).name);
    }

    public static GrpcKitChannelBuilder forConfig(String srcAppName, String dstAppName,
                                                  @Nonnull GrpcKitConfig config) {
        String registry = config.validate().getNameResolver().getRegistry();
        ApplicationNameValidator.validateWithRegistry(dstAppName, registry);
        return new GrpcKitChannelBuilder(srcAppName, dstAppName, config.validate());
    }

    public GrpcKitChannelBuilder customize(GrpcKitChannelBuilderFactory factory) {
        return factory.customize(this);
    }

    @Override
    protected ManagedChannelBuilder<?> delegate() {
        return managedChannelBuilder;
    }

    @Override
    public GrpcKitChannelBuilder executor(Executor executor) {
        if (executor != null) {
            /*
             * Attributes that SHOULD be included on metric events.
             *
             * @see https://opentelemetry.io/docs/reference/specification/metrics/semantic_conventions/rpc/
             */
            Tags tags = Tags.of(
                Tag.of("rpc.system", "grpc"),
                Tag.of("app.name", srcAppName),
                Tag.of("app.peer.name", dstAppName),
                Tag.of("app.group", config.getGroup())
            );
            String executorName = "grpc_channel_" + dstAppName;
            String metricPrefix = "rpc.channel";
            /*
             * Add executor metrics to the global registry.
             *
             * @see https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/5292
             * @see https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/9058ad6f40a75d15a70a69d7fe32ff2c19b05a00/instrumentation/micrometer/micrometer-1.5/javaagent/src/main/java/io/opentelemetry/javaagent/instrumentation/micrometer/v1_5/MetricsInstrumentation.java#L35
             */
            executor = ExecutorServiceMetrics.monitor(Metrics.globalRegistry, executor,
                executorName, metricPrefix, tags);
        }
        return super.executor(executor);
    }
}
