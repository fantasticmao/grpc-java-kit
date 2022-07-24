package cn.fantasticmao.grpckit.boot.metadata;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A loader for {@link ApplicationMetadata application metadata}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
public interface ApplicationMetadataLoader {

    List<ApplicationMetadata> load(@Nullable ClassLoader classLoader);
}
