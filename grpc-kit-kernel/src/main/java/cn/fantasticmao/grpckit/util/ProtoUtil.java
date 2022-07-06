package cn.fantasticmao.grpckit.util;

import cn.fantasticmao.grpckit.GrpcKitException;
import io.grpc.ServiceDescriptor;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An util class for {@code Protocol Buffers}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-25
 */
public class ProtoUtil {

    /**
     * Get the {@link ServiceDescriptor ServiceDescriptor} of the gRPC service by the stub class.
     *
     * @param clazz the gRPC stub class.
     * @return the descriptor for the service.
     */
    public static <S extends AbstractStub<S>> ServiceDescriptor getServiceDescriptor(Class<S> clazz) {
        final String grpcName = getGrpcName(clazz);
        final Class<?> grpcClazz;
        try {
            grpcClazz = Class.forName(grpcName);
        } catch (ClassNotFoundException e) {
            throw new GrpcKitException("Cannot found gRPC class for StubType: " + clazz.getName(), e);
        }

        final Method getServiceDescriptorMethod;
        try {
            getServiceDescriptorMethod = grpcClazz.getMethod("getServiceDescriptor");
        } catch (NoSuchMethodException e) {
            throw new GrpcKitException("Cannot found \"getServiceDescriptor\" method for gRPC class: "
                + grpcClazz.getName(), e);
        }

        try {
            return (ServiceDescriptor) getServiceDescriptorMethod.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new GrpcKitException("Cannot invoke \"getServiceDescriptor\" method on gRPC class: "
                + grpcClazz.getName(), e);
        }
    }

    /**
     * Get the gRPC generated Java class name by the stub class.
     *
     * @param clazz the gRPC stub class.
     * @return the Java class name that gRPC generated.
     * @see <a href="https://grpc.io/docs/languages/java/generated-code/#packages">Naming convention for the gRPC Java class</a>
     */
    static <S extends AbstractStub<S>> String getGrpcName(Class<S> clazz) {
        final String stubSuffix = getStubSuffix(clazz);
        final String stubClassName = clazz.getSimpleName();
        final String serviceName = stubClassName.substring(0, stubClassName.length() - stubSuffix.length());
        return clazz.getPackageName() + "." + serviceName + "Grpc";
    }

    /**
     * Get the suffix of the name by the stub class.
     *
     * @param clazz the gRPC stub class.
     * @return the suffix of the stub name.
     * @see <a href="https://grpc.io/docs/languages/java/generated-code/#client-stubs">Naming convention for gRPC Stubs</a>
     */
    static <S extends AbstractStub<S>> String getStubSuffix(Class<S> clazz) {
        if (AbstractAsyncStub.class.isAssignableFrom(clazz)) {
            return "Stub";
        } else if (AbstractBlockingStub.class.isAssignableFrom(clazz)) {
            return "BlockingStub";
        } else if (AbstractFutureStub.class.isAssignableFrom(clazz)) {
            return "FutureStub";
        } else {
            throw new IllegalArgumentException(clazz.getName() + " is not a standard gRPC Stub");
        }
    }
}
