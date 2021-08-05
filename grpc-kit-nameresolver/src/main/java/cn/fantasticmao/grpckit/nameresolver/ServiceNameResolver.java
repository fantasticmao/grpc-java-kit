package cn.fantasticmao.grpckit.nameresolver;

/**
 * ServiceNameResolver
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021-08-06
 */
public interface ServiceNameResolver {

    interface Registry {

        void doRegistry(String path, String data) throws Exception;

    }

    interface Discovery {

        String lookup(String path) throws Exception;

    }
}
