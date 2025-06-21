package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Value("${post-service.async.core-pool-size}")
    private int corePoolSize;

    @Value("${post-service.async.max-pool-size}")
    private int maxPoolSize;

    @Value("${post-service.async.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "postEventExecutor")
    public Executor postEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("PostEventExecutor-");
        executor.initialize();
        return executor;
    }
}
