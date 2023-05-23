package com.isxcode.star.backend.config;

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

  @Bean("sparkYunWorkThreadPool")
  public Executor sparkYunWorkThreadPool() {

    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(100);
    threadPoolTaskExecutor.setMaxPoolSize(200);
    threadPoolTaskExecutor.setQueueCapacity(200);
    threadPoolTaskExecutor.setKeepAliveSeconds(60);
    threadPoolTaskExecutor.setAllowCoreThreadTimeOut(false);
    threadPoolTaskExecutor.setThreadNamePrefix("sparkYunWorkThreadPool-");
    threadPoolTaskExecutor.setRejectedExecutionHandler((r, executor) -> log.info("未执行的异常进程"));
    threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    threadPoolTaskExecutor.setAwaitTerminationSeconds(300);
    return threadPoolTaskExecutor;
  }
}
