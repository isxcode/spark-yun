package com.isxcode.star.common.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    public static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    public static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    public static final ThreadLocal<Boolean> JPA_TENANT_MODE = new ThreadLocal<>();
}
