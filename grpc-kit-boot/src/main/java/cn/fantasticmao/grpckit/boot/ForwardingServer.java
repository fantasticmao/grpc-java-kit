package cn.fantasticmao.grpckit.boot;

import com.google.common.base.MoreObjects;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Server} that delegates all its methods to another by default.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-21
 */
class ForwardingServer extends Server {
    private final Server delegate;

    protected ForwardingServer(Server server) {
        this.delegate = server;
    }

    @Override
    public Server start() throws IOException {
        return delegate.start();
    }

    @Override
    public int getPort() {
        return delegate.getPort();
    }

    @Override
    public List<? extends SocketAddress> getListenSockets() {
        return delegate.getListenSockets();
    }

    @Override
    public List<ServerServiceDefinition> getServices() {
        return delegate.getServices();
    }

    @Override
    public List<ServerServiceDefinition> getImmutableServices() {
        return delegate.getImmutableServices();
    }

    @Override
    public List<ServerServiceDefinition> getMutableServices() {
        return delegate.getMutableServices();
    }

    @Override
    public Server shutdown() {
        return delegate.shutdown();
    }

    @Override
    public Server shutdownNow() {
        return delegate.shutdown();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        delegate.awaitTermination();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("delegate", delegate)
            .toString();
    }
}
