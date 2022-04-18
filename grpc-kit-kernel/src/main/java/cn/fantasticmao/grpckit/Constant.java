package cn.fantasticmao.grpckit;

import io.grpc.CallOptions;

/**
 * Some constants.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-08-04
 */
public interface Constant {
    String VERSION = "1.39.0";

    CallOptions.Key<String> KEY_OPTION_TAG = CallOptions.Key
        .createWithDefault("tag", ServiceMetadata.DEFAULT_TAG);
}
