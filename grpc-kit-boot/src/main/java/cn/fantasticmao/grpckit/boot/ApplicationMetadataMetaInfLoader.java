package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.GrpcKitException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * A loader for {@link ApplicationMetadata application metadata}, load from {@value #METADATA_RESOURCE_LOCATION} files.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
public class ApplicationMetadataMetaInfLoader implements ApplicationMetadataLoader {
    private static final String METADATA_RESOURCE_LOCATION = "META-INF/application-metadata.yml";

    private final Yaml yaml;

    public ApplicationMetadataMetaInfLoader() {
        this.yaml = new Yaml(new Constructor(ApplicationMetadata.class));
    }

    @Override
    public List<ApplicationMetadata> load(@Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = Thread.currentThread().getContextClassLoader();
        }

        final Enumeration<URL> urls;
        try {
            urls = classLoaderToUse.getResources(METADATA_RESOURCE_LOCATION);
        } catch (IOException e) {
            throw new GrpcKitException("Unable to load application metadata from location: "
                + METADATA_RESOURCE_LOCATION, e);
        }

        List<ApplicationMetadata> metadataList = new LinkedList<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try (InputStream input = url.openStream()) {
                ApplicationMetadata metadata = yaml.load(input);
                metadataList.add(metadata);
            } catch (IOException | YAMLException e) {
                throw new GrpcKitException("Unable to load application metadata from URL: "
                    + url, e);
            }
        }
        return metadataList;
    }
}
