package cn.fantasticmao.grpckit.boot.config;

import cn.fantasticmao.grpckit.GrpcKitException;

import javax.annotation.Nonnull;

/**
 * A loader for {@link GrpcKitConfig gRPC Java Kit Configutration}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-25
 */
public interface GrpcKitConfigLoader {
    GrpcKitConfigLoader YAML = new GrpcKitConfigYamlLoader();

    /**
     * Load and parse {@link GrpcKitConfig} from the specific file.
     *
     * @param path the config file path
     * @return a new {@link GrpcKitConfig} instance
     * @throws GrpcKitException errors during loading and parsing phases
     */
    GrpcKitConfig loadAndParse(@Nonnull String path);
}
