package cn.fantasticmao.grpckit.support;

import cn.fantasticmao.grpckit.ServiceMetadata;
import io.grpc.CallOptions;

/**
 * An util class for {@link CallOptions gRPC CallOptions}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-05-03
 */
public interface CallOptionUtil {
    CallOptions.Key<String> KEY_TAG = CallOptions.Key
        .createWithDefault("tag", ServiceMetadata.DEFAULT_TAG);
}
