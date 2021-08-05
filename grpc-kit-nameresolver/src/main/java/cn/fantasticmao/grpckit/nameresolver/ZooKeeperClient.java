package cn.fantasticmao.grpckit.nameresolver;

import cn.fantasticmao.grpckit.common.config.GrpcKitConfig;
import cn.fantasticmao.grpckit.common.constant.Constant;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ZooKeeperClient
 *
 * @author maomao
 * @version 1.39.0
 * @since 2021/8/5
 */
public class ZooKeeperClient {
    private static ZooKeeperClient instance;

    private static final Logger LOGGER = Logger.getLogger(ZooKeeperClient.class.getName());
    private final ZooKeeper zooKeeper;

    public static ZooKeeperClient getInstance() {
        if (instance == null) {
            synchronized (ZooKeeperClient.class) {
                if (instance == null) {
                    instance = new ZooKeeperClient();
                }
            }
        }
        return instance;
    }

    private ZooKeeperClient() {
        String connectString = GrpcKitConfig.getInstance().getValue(Constant.ConfigKey.ZOOKEEPER_CONNECT_STRING);
        Objects.requireNonNull(connectString, "ZooKeeper connect string can not be null");

        int sessionTimeout;
        int defaultSessionTimeout = 5_000;
        String sessionTimeoutStr = GrpcKitConfig.getInstance().getValue(Constant.ConfigKey.ZOOKEEPER_SESSION_TIMEOUT,
            String.valueOf(defaultSessionTimeout));
        try {
            sessionTimeout = Integer.parseInt(sessionTimeoutStr);
        } catch (NumberFormatException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "ZooKeeper session timeout number format error, fallback to " +
                    defaultSessionTimeout + " ms", e);
            }
            sessionTimeout = defaultSessionTimeout;
        }

        try {
            this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, event -> {
                if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())
                    && Watcher.Event.EventType.None.equals(event.getType())) {
                    LOGGER.info("Watch ZooKeeper connected ...");
                }
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Connect to ZooKeeper error", e);
            throw new RuntimeException(e);
        }
    }

    public byte[] getDataByPath(String path) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path, false, null);
    }

    public void setDataByPath(String path, byte[] data) throws KeeperException, InterruptedException {
        zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
}
