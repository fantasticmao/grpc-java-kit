package cn.fantasticmao.grpckit.common.config;

import cn.fantasticmao.grpckit.common.constant.Constant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GrpcKitConfig
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021-08-04
 */
public class GrpcKitConfig {
    private static volatile GrpcKitConfig instance;

    private static final Logger LOGGER = Logger.getLogger(GrpcKitConfig.class.getName());
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

    public GrpcKitConfig() {
        this.properties = new Properties();
    }

    private void load() {
        URL configUrl = Thread.currentThread().getContextClassLoader().getResource(Constant.CONFIG_FILE_PATH);
        if (configUrl == null) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Not exists grpc-kit config file: \"" + Constant.CONFIG_FILE_PATH + "\"");
            }
            return;
        }

        try (FileInputStream in = new FileInputStream(configUrl.getPath())) {
            properties.load(in);
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Load grpc-kit config file: \"" + Constant.CONFIG_FILE_PATH + "\" error", e);
            }
        }
    }

    @Nullable
    public String getValue(Constant.ConfigKey key) {
        return properties.getProperty(key.code);
    }

    @Nonnull
    public String getValue(Constant.ConfigKey key, @Nonnull String defaultValue) {
        return properties.getProperty(key.code, defaultValue);
    }
}
