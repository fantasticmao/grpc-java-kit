package cn.fantasticmao.grpckit;

import com.google.gson.Gson;

/**
 * Some constants.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2021-08-04
 */
public interface Constant {
    String CONFIG_FILE_PATH = System.getProperty("cn.fantasticmao.grpckit.config", "grpc-kit.yaml");

    String VERSION = "1.39.0";

    Gson GSON = new Gson();
}
