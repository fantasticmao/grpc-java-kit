package cn.fantasticmao.grpckit.boot.factory;

import cn.fantasticmao.grpckit.boot.GrpcKitServerBuilder;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.protobuf.services.ProtoReflectionService;

/**
 * A factory that allows users to customize the building of the {@link GrpcKitServerBuilder GrpcKitServerBuilder}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-05-04
 */
public interface GrpcKitServerBuilderFactory {

    GrpcKitServerBuilder customize(GrpcKitServerBuilder builder);

    enum Default implements GrpcKitServerBuilderFactory {
        INSTANCE;

        @Override
        public GrpcKitServerBuilder customize(GrpcKitServerBuilder builder) {
            return builder
                .addService(new HealthStatusManager().getHealthService())
                .addService(ProtoReflectionService.newInstance());
        }
    }
}
