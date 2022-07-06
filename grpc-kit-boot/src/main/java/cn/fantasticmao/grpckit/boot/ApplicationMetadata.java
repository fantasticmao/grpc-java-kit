package cn.fantasticmao.grpckit.boot;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Application metadata provided by gRPC services for gRPC clients.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
@Getter
@Setter
public final class ApplicationMetadata {
    /**
     * Name of the application.
     */
    private String name;

    /**
     * All service names registered in the gRPC server.
     */
    private List<String> services;

    public static final Pattern NAME_PATTERN = Pattern.compile("\\w+");

    public ApplicationMetadata validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("name must match the pattern: " + NAME_PATTERN.pattern());
        }
        return this;
    }
}
