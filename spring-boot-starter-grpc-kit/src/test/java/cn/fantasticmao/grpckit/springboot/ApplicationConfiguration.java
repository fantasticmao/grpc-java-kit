package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.boot.factory.GrpcKitChannelBuilderFactory;
import cn.fantasticmao.grpckit.boot.factory.GrpcKitServerBuilderFactory;
import cn.fantasticmao.grpckit.boot.factory.GrpcKitThreadFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

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
    public GrpcKitChannelBuilderFactory grpcKitChannelBuilderFactory() {
        ExecutorService executorService = new ThreadPoolExecutor(5, 50,
            10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(200),
            new GrpcKitThreadFactory.Channel("unit_test_spring_boot"));
        return builder -> GrpcKitChannelBuilderFactory.Default.INSTANCE.customize(builder)
            .executor(executorService);
    }

    @Bean
    public GrpcKitServerBuilderFactory grpcKitServerBuilderFactory() {
        ExecutorService executorService = new ThreadPoolExecutor(5, 100,
            10, TimeUnit.MINUTES, new SynchronousQueue<>(),
            new GrpcKitThreadFactory.Server("unit_test_spring_boot"));
        return (builder) -> GrpcKitServerBuilderFactory.Default.INSTANCE.customize(builder)
            .executor(executorService);
    }
}
