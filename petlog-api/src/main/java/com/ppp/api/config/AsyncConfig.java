package com.ppp.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        int processorSize = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processorSize);
        executor.setMaxPoolSize(processorSize * 2);
        executor.setQueueCapacity(processorSize * 10);
        executor.initialize();
        return executor;
    }
}
