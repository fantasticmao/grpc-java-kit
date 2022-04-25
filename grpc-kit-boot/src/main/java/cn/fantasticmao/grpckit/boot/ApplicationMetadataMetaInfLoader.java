package cn.fantasticmao.grpckit.boot;

import javax.annotation.Nullable;
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

    @Override
    public List<ApplicationMetadata> load(@Nullable ClassLoader classLoader) {
        // TODO
        return null;
    }
}
