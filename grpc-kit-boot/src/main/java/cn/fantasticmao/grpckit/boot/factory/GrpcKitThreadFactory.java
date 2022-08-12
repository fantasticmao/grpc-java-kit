package cn.fantasticmao.grpckit.boot.factory;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link ThreadFactory thread factory} for building gRPC {@link Server server}
 * and {@link Channel channel} executors.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see io.grpc.ServerBuilder#executor(Executor)
 * @see io.grpc.ManagedChannelBuilder#executor(Executor)
 * @since 2022-08-12
 */
public abstract class GrpcKitThreadFactory implements ThreadFactory {
    private final String namePrefix;
    private final AtomicInteger threadNumber;
    private final ThreadGroup group;

    protected GrpcKitThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
        this.threadNumber = new AtomicInteger(1);

        SecurityManager securityManager = System.getSecurityManager();
        this.group = (securityManager != null)
            ? securityManager.getThreadGroup()
            : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        Thread thread = new Thread(group, runnable,
            namePrefix + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }

    public static class Server extends GrpcKitThreadFactory {

        public Server(String appName) {
            super("grpc-server-" + appName + "-exec-");
        }
    }

    public static class Channel extends GrpcKitThreadFactory {

        public Channel(String dstAppName) {
            super("grpc-channel-" + dstAppName + "-exec-");
        }
    }
}
