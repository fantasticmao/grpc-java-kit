package cn.fantasticmao.grpckit.springboot.annotation;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "gRPC Client".
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-03
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GrpcClient {
    String value() default "";

    String tag() default "";

    int timeout() default 5_000;

}
