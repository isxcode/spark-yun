package com.isxcode.star.config;

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

  @Bean("springEventThreadPool")
  public Executor springEventThreadPool() {

    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(10); // 核心线程数，指正在执行的线程，要小于MaxPoolSize配置
    threadPoolTaskExecutor.setMaxPoolSize(200); // 允许最大的线程数量
    threadPoolTaskExecutor.setQueueCapacity(10); // 在最大线程数上，再扩展等待线程数
    threadPoolTaskExecutor.setThreadNamePrefix("springEventThreadPool-"); // 设置线程名的前缀
    threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true); // true表示空闲线程会回收，false表示空闲的线程不会回收
    threadPoolTaskExecutor.setKeepAliveSeconds(100); // 用于设置线程池中空闲线程的存活时间
    threadPoolTaskExecutor.setPrestartAllCoreThreads(
        true); // 是否需要一下子把线程创建满，提高速度，ture会把核心线程创满，即使没有任务。
    threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(
        true); // 设置线程池关闭时是否等待，将任务全部提交后，再关闭线程池
    threadPoolTaskExecutor.setAwaitTerminationSeconds(300); // 手动关闭停止线程池的时候，线程池还会停留的时间， 0表示不等待任务完成。
    //    threadPoolTaskExecutor.setTaskDecorator(new CustomTaskDecorator()); // 控制任务执行前，执行后
    threadPoolTaskExecutor.setRejectedExecutionHandler(
        (r, executor) -> log.info("未执行的异常进程" + executor.toString())); // 被拒绝的事件
    return threadPoolTaskExecutor;
  }
}
