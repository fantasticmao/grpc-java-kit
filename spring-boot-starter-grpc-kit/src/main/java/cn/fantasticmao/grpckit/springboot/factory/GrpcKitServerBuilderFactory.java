package cn.fantasticmao.grpckit.springboot.factory;

import cn.fantasticmao.grpckit.boot.GrpcKitServerBuilder;
import io.grpc.ServerServiceDefinition;

import java.util.List;

/**
 * A factory that allows users to customize the building of the {@link GrpcKitServerBuilder GrpcKitServerBuilder}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-05-04
 */
public interface GrpcKitServerBuilderFactory {

    GrpcKitServerBuilder customize(GrpcKitServerBuilder builder, List<ServerServiceDefinition> services);

    enum Default implements GrpcKitServerBuilderFactory {
        INSTANCE;

        @Override
        public GrpcKitServerBuilder customize(GrpcKitServerBuilder builder, List<ServerServiceDefinition> services) {
            return builder
                .addServices(services);
        }
    }
}
