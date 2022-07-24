package cn.fantasticmao.grpckit.boot.config;

import cn.fantasticmao.grpckit.GrpcKitException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * GrpcKitConfigYamlLoader
 *
 * @author fantasticmao
 * @since 2022-07-25
 */
class GrpcKitConfigYamlLoader implements GrpcKitConfigLoader {
    private final Yaml yaml;

    public GrpcKitConfigYamlLoader() {
        this.yaml = new Yaml(new Constructor(GrpcKitConfig.class));
    }

    @Override
    public GrpcKitConfig loadAndParse(@Nonnull String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(path);
        Objects.requireNonNull(url, "File not found: " + path);

        try (InputStream input = url.openStream()) {
            GrpcKitConfig config = yaml.load(input);
            return config.validate();
        } catch (IOException | YAMLException e) {
            throw new GrpcKitException("Unable to load GrpcKitConfig from: " + path, e);
        }
    }
}
