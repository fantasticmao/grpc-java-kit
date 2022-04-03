package cn.fantasticmao.grpckit.springboot.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Grpc Service Annotation.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-03
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface GrpcService {
    String value() default "";
}
