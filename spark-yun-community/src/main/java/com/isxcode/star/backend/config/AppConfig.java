package com.isxcode.star.backend.config;

import com.isxcode.star.api.properties.SparkYunProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 应用配置中心.
 */
@Configuration
@EnableCaching
@EnableTransactionManagement
@EnableConfigurationProperties(SparkYunProperties.class)
public class AppConfig {
}
