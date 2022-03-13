package cn.fantasticmao.grpckit.common;

/**
 * GrpcKitException
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-13
 */
public class GrpcKitException extends RuntimeException {

    public GrpcKitException(String message) {
        super(message);
    }

    public GrpcKitException(String message, Throwable cause) {
        super(message, cause);
    }
}
