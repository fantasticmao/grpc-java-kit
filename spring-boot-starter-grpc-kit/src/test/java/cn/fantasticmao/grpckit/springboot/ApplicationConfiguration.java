package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.boot.factory.GrpcKitServerBuilderFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ApplicationConfiguration
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-04-04
 */
@SpringBootApplication
public class ApplicationConfiguration {

    @Bean
    public GrpcKitServerBuilderFactory grpcKitServerBuilderFactory() {
        ExecutorService executorService = new ThreadPoolExecutor(5, 100,
            10, TimeUnit.MINUTES, new SynchronousQueue<>());
        return (builder) -> GrpcKitServerBuilderFactory.Default.INSTANCE.customize(builder)
            .executor(executorService);
    }
}
