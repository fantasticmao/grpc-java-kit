package cn.fantasticmao.grpckit.springboot;

import cn.fantasticmao.grpckit.GrpcKitFactory;
import cn.fantasticmao.grpckit.springboot.annotation.GrpcService;
import io.grpc.BindableService;
import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * An {@link ApplicationListener} for start the gRPC {@link Server Server}.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events">Standard and Custom Events</a>
 * @since 2022-04-13
 */
public class GrpcServerStartApplicationListener implements ApplicationListener<ContextRefreshedEvent>,
    ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerStartApplicationListener.class);

    private final AtomicBoolean started;
    @Nullable
    private ConfigurableApplicationContext applicationContext;

    public GrpcServerStartApplicationListener() {
        this.started = new AtomicBoolean(false);
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        if (!(applicationContext instanceof ConfigurableApplicationContext)) {
            throw new IllegalStateException("applicationContext must be a subtype of ConfigurableApplicationContext");
        }
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        Objects.requireNonNull(this.applicationContext, "applicationContext must not be null");

        if (!this.started.compareAndSet(false, true)) {
            LOGGER.debug("The gRPC server has already been started");
            return;
        }

        GrpcKitFactory grpcKitFactory = this.applicationContext.getBean(GrpcKitFactory.class);
        Objects.requireNonNull(grpcKitFactory, "bean 'grpcKitFactory' in applicationContext must not be null");

        Set<Map.Entry<String, Object>> grpcServiceBeans = this.applicationContext
            .getBeansWithAnnotation(GrpcService.class).entrySet().stream()
            .filter(entry -> entry.getValue() instanceof BindableService)
            .collect(Collectors.toSet());
        if (grpcServiceBeans.isEmpty()) {
            return;
        }

        Set<String> grpcServiceNames = grpcServiceBeans.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        Set<BindableService> grpcServices = grpcServiceBeans.stream()
            .map(Map.Entry::getValue)
            .map(obj -> (BindableService) obj)
            .collect(Collectors.toSet());

        Server grpcServer = grpcKitFactory.newAndStartServer(grpcServices);
        if (grpcServer != null) {
            this.registerBeanForGrpcServer(grpcServer);
            this.publishGrpcServiceStartedEvent(event, grpcServiceNames);
        }
    }

    private void registerBeanForGrpcServer(Server grpcServer) {
        Objects.requireNonNull(this.applicationContext, "applicationContext must not be null");
        ConfigurableListableBeanFactory beanFactory = this.applicationContext.getBeanFactory();
        beanFactory.registerSingleton(GrpcKitAutoConfiguration.BEAN_NAME_GRPC_KIT_SERVER, grpcServer);
    }

    private void publishGrpcServiceStartedEvent(ContextRefreshedEvent sourceEvent, Set<String> serviceNames) {
        Objects.requireNonNull(this.applicationContext, "applicationContext must not be null");
        ApplicationEvent grpcServiceStartedEvent = new GrpcServerStartedEvent(sourceEvent, serviceNames);
        this.applicationContext.publishEvent(grpcServiceStartedEvent);
    }
}
