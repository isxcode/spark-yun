package com.isxcode.star.backend.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** 应用配置中心. */
@Configuration
@EnableCaching
@EnableTransactionManagement
public class AppConfig {}
