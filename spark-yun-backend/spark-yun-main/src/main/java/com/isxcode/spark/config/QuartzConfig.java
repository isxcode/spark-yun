package com.isxcode.spark.config;

import com.alibaba.druid.util.JdbcUtils;
import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@AutoConfigureBefore(QuartzAutoConfiguration.class)
@EnableConfigurationProperties({DataSourceProperties.class, QuartzProperties.class, IsxAppProperties.class})
@ConditionalOnClass(JdbcUtils.class)
@RequiredArgsConstructor
public class QuartzConfig {

    private final QuartzProperties quartzProperties;

    private final DataSourceProperties dataSourceProperties;

    private final IsxAppProperties isxAppProperties;

    @PostConstruct
    public void changeQuartzProperties() {

        if ("simple".equals(isxAppProperties.getConfigMode())) {
            if (DatasourceDriver.POSTGRE_SQL_DRIVER.equals(dataSourceProperties.getDriverClassName())) {
                quartzProperties.getProperties().put("org.quartz.jobStore.driverDelegateClass",
                    "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
            }
        }

        // 配置 Quartz 线程池，控制并发执行
        quartzProperties.getProperties().put("org.quartz.threadPool.threadCount", "10");
        quartzProperties.getProperties().put("org.quartz.threadPool.threadPriority", "5");
        quartzProperties.getProperties().put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");

        // 配置 JobStore 的 misfire 阈值
        quartzProperties.getProperties().put("org.quartz.jobStore.misfireThreshold", "60000");
    }

    @Bean
    @Primary
    public QuartzProperties quartzProperties() {
        return quartzProperties;
    }
}
