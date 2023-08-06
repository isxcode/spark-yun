package com.isxcode.star.config;

import com.isxcode.star.backend.api.base.properties.SparkYunProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** 应用配置中心. */
@Configuration
@EnableCaching
@EnableTransactionManagement
@EnableConfigurationProperties(SparkYunProperties.class)
@EnableJpaAuditing(auditorAwareRef = "jpaAuditorConfig")
public class AppConfig {}
