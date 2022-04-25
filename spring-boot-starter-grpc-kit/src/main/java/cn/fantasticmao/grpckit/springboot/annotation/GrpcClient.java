package cn.fantasticmao.grpckit.springboot.annotation;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "gRPC client".
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-03
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcClient {

    String tag() default "";

    int timeout() default 2_000;

}
