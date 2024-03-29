package cn.fantasticmao.grpckit.boot.factory;

import cn.fantasticmao.grpckit.boot.GrpcKitChannelBuilder;

/**
 * A factory that allows users to customize the building of the {@link GrpcKitChannelBuilder GrpcKitChannelBuilder}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-05-04
 */
public interface GrpcKitChannelBuilderFactory {

    GrpcKitChannelBuilder customize(GrpcKitChannelBuilder builder);

    enum Default implements GrpcKitChannelBuilderFactory {
        INSTANCE;

        @Override
        public GrpcKitChannelBuilder customize(GrpcKitChannelBuilder builder) {
            return builder
                .usePlaintext();
        }
    }
}
