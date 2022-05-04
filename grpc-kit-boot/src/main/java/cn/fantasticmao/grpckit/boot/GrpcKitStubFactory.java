package cn.fantasticmao.grpckit.boot;

import cn.fantasticmao.grpckit.GrpcKitException;
import cn.fantasticmao.grpckit.support.CallOptionUtil;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

/**
 * A factory for creating {@link io.grpc.stub.AbstractStub gRPC Stub} instances.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-21
 */
public class GrpcKitStubFactory {

    public static <S extends AbstractStub<S>> S newStub(Class<S> clazz, @Nonnull Channel channel,
                                                        @Nonnull GrpcKitConfig config) {
        String tag = config.getGrpc().getClient().getTag();
        int timeout = config.getGrpc().getClient().getTimeout();
        return newStub(clazz, channel, tag, timeout);
    }

    public static <S extends AbstractStub<S>> S newStub(Class<S> clazz, @Nonnull Channel channel,
                                                        String tag, int timeout) {
        final AbstractStub.StubFactory<S> stubFactory = getStubFactory(clazz);
        final S stub;
        if (AbstractAsyncStub.class.isAssignableFrom(clazz)) {
            stub = AbstractAsyncStub.newStub(stubFactory, channel);
        } else if (AbstractBlockingStub.class.isAssignableFrom(clazz)) {
            stub = AbstractBlockingStub.newStub(stubFactory, channel);
        } else if (AbstractFutureStub.class.isAssignableFrom(clazz)) {
            stub = AbstractFutureStub.newStub(stubFactory, channel);
        } else {
            throw new IllegalArgumentException(clazz.getName() + " is not a standard gRPC Stub");
        }
        return stub.withOption(CallOptionUtil.KEY_TAG, tag)
            .withDeadlineAfter(timeout, TimeUnit.MILLISECONDS);
    }

    private static <S extends AbstractStub<S>> AbstractStub.StubFactory<S> getStubFactory(Class<S> clazz) {
        return (channel, callOptions) -> {
            final Constructor<S> constructor;
            try {
                // get private constructor
                constructor = clazz.getDeclaredConstructor(Channel.class, CallOptions.class);
                if ((!Modifier.isPublic(constructor.getModifiers()) ||
                    !Modifier.isPublic(constructor.getDeclaringClass().getModifiers())) && !constructor.canAccess(null)) {
                    constructor.setAccessible(true);
                }
            } catch (NoSuchMethodException e) {
                throw new GrpcKitException("Get gRPC stub constructor error", e);
            }

            try {
                return constructor.newInstance(channel, callOptions);
            } catch (ReflectiveOperationException e) {
                throw new GrpcKitException("New gRPC stub instance error", e);
            }
        };
    }
}
