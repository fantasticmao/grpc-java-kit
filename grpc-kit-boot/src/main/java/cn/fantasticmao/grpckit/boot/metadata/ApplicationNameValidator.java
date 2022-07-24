package cn.fantasticmao.grpckit.boot.metadata;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * Validator for application names.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-07-13
 */
public interface ApplicationNameValidator {

    void validate(String name) throws IllegalArgumentException;

    static void validateWithRegistry(String name, String registry) throws IllegalArgumentException {
        URI registryUri = URI.create(registry);

        final ApplicationNameValidator validator;
        if ("dns".equals(registryUri.getScheme())) {
            validator = Dns.INSTANCE;
        } else {
            validator = Canonic.INSTANCE;
        }
        validator.validate(name);
    }

    enum Canonic implements ApplicationNameValidator {
        INSTANCE;

        private final Pattern namePattern = Pattern.compile("\\w+");

        @Override
        public void validate(String name) throws IllegalArgumentException {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Application name must not be null or blank");
            }
            if (!namePattern.matcher(name).matches()) {
                throw new IllegalArgumentException("Canonical application name: " + name
                    + " must match the pattern: " + namePattern.pattern());
            }
        }
    }

    enum Dns implements ApplicationNameValidator {
        INSTANCE;

        @Override
        public void validate(String name) throws IllegalArgumentException {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Application name must not be null or blank");
            }
            URI nameUri = URI.create("//" + name);
            if (nameUri.getHost() == null || nameUri.getAuthority() == null) {
                throw new IllegalArgumentException("DNS application name: " + name + " is invalid");
            }
        }
    }

}
