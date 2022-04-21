package cn.fantasticmao.grpckit.nameresolver.nacos;

import cn.fantasticmao.grpckit.GrpcKitException;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harrison
 * @date 2022/4/21
 */
public class NamingServiceHolder {

    private NamingServiceHolder() { }

    private static final ConcurrentHashMap<String, NamingService> NAMING_SERVICE_CACHE = new ConcurrentHashMap<>();

    public static NamingService get(String serverAddress, String namespaceId) {
        String key = String.join("_", serverAddress, namespaceId);
        // double check
        if (!NAMING_SERVICE_CACHE.containsKey(key)) {
            synchronized (NamingServiceHolder.class) {
                if (!NAMING_SERVICE_CACHE.containsKey(key)) {
                    NamingService namingService = newNameService(serverAddress, namespaceId);
                    NAMING_SERVICE_CACHE.put(key, namingService);
                }
            }
        }
        return NAMING_SERVICE_CACHE.get(key);
    }

    private static NamingService newNameService(String serverAddress, String namespaceId) {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", serverAddress);
            properties.setProperty("namespace", namespaceId);
            return NacosFactory.createNamingService(properties);
        } catch (NacosException e) {
            String errorMsg = String.format("create namingService error, serverAddr = %s, namespaceId = %s", serverAddress, namespaceId);
            throw new GrpcKitException(errorMsg);
        }
    }
}
