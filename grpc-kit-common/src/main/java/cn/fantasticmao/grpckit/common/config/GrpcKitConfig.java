package cn.fantasticmao.grpckit.common.config;

import cn.fantasticmao.grpckit.common.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * GrpcKitConfig
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-08-04
 */
public class GrpcKitConfig {
    private static volatile GrpcKitConfig instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcKitConfig.class);
    private final Properties properties;

    public static GrpcKitConfig getInstance() {
        if (instance == null) {
            synchronized (GrpcKitConfig.class) {
                if (instance == null) {
                    instance = new GrpcKitConfig();
                    instance.load();
                }
            }
        }
        return instance;
    }

    private GrpcKitConfig() {
        this.properties = new Properties();
    }

    private void load() {
        URL configUrl = Thread.currentThread().getContextClassLoader().getResource(Constant.CONFIG_FILE_PATH);
        if (configUrl == null) {
            LOGGER.warn("Not exists grpc-kit config file: \"{}\"", Constant.CONFIG_FILE_PATH);
            return;
        }

        try (FileInputStream in = new FileInputStream(configUrl.getPath())) {
            properties.load(in);
        } catch (IOException e) {
            LOGGER.error("Load grpc-kit config file: \"{}\" error", Constant.CONFIG_FILE_PATH, e);
        }
    }

    @Nullable
    public String getValue(GrpcKitConfigKey key) {
        return properties.getProperty(key.code);
    }

    @Nonnull
    public String getValue(GrpcKitConfigKey key, @Nonnull String defaultValue) {
        return properties.getProperty(key.code, defaultValue);
    }

    @Nonnull
    public Integer getIntValue(GrpcKitConfigKey key, @Nonnull Integer defaultValue) {
        String value = properties.getProperty(key.code, String.valueOf(defaultValue));
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            LOGGER.error("Convert grpc-kit config value \"{}\" to Integer error, fall back to default value \"{}\"",
                value, defaultValue, e);
            return defaultValue;
        }
    }
}
