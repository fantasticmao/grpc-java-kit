package cn.fantasticmao.grpckit.boot.config;

import cn.fantasticmao.grpckit.GrpcKitException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * A <a href="https://yaml.org/">YAML</a> based loader for {@link GrpcKitConfig gRPC Java Kit Configutration}.
 *
 * @author fantasticmao
 * @since 2022-07-25
 */
class GrpcKitConfigYamlLoader implements GrpcKitConfigLoader {
    private final Yaml yaml;

    public GrpcKitConfigYamlLoader() {
        this.yaml = this.createYaml();
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

    private Yaml createYaml() {
        Constructor constructor = new Constructor(GrpcKitConfig.class);
        constructor.setAllowDuplicateKeys(false);
        constructor.setPropertyUtils(new RelaxedBindingPropertyUtils());
        return new Yaml(constructor);
    }

    /**
     * A {@link PropertyUtils} that supports relaxed binding similar to Spring Boot.
     *
     * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding">Relaxed Binding</a>
     */
    static class RelaxedBindingPropertyUtils extends PropertyUtils {
        private static final char HYPHEN = '-';
        private static final char UNDERSCORE = '_';

        RelaxedBindingPropertyUtils() {
            super();
        }

        @Override
        public Property getProperty(Class<?> type, String originalName, BeanAccess bAccess) {
            String name = this.convertName(originalName);
            return super.getProperty(type, name, bAccess);
        }

        String convertName(String name) {
            StringBuilder out = new StringBuilder(name.length());
            boolean toUpper = false;
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (HYPHEN == ch || UNDERSCORE == ch) {
                    toUpper = true;
                    continue;
                }
                if (toUpper) {
                    out.append(Character.toUpperCase(ch));
                    toUpper = false;
                } else {
                    out.append(ch);
                }
            }
            return out.toString();
        }
    }
}
