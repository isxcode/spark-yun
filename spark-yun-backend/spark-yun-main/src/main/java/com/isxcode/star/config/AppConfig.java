package com.isxcode.star.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** 应用配置中心. */
@Configuration
@EnableCaching
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "jpaAuditorConfig")
public class AppConfig {}
