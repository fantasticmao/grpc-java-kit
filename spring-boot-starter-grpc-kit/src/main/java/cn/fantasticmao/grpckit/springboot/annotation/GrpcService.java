package cn.fantasticmao.grpckit.springboot.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "gRPC Service".
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface GrpcService {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
