package com.isxcode.star.config;

import com.alibaba.druid.util.JdbcUtils;
import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Slf4j
@Configuration
@AutoConfigureBefore(FlywayAutoConfiguration.class)
@EnableConfigurationProperties({DataSourceProperties.class, FlywayProperties.class, IsxAppProperties.class})
@ConditionalOnClass(JdbcUtils.class)
@RequiredArgsConstructor
public class FlywayConfig {

    private final FlywayProperties flywayProperties;

    private final DataSourceProperties dataSourceProperties;

    private final IsxAppProperties isxAppProperties;

    @PostConstruct
    public void changeFlywayProperties() {

        if ("simple".equals(isxAppProperties.getConfigMode())) {
            flywayProperties.setDriverClassName(dataSourceProperties.getDriverClassName());
            flywayProperties.setUrl(dataSourceProperties.getUrl());
            flywayProperties.setUser(dataSourceProperties.getUsername());
            flywayProperties.setPassword(dataSourceProperties.getPassword());
            if (DatasourceDriver.H2_DRIVER.equals(dataSourceProperties.getDriverClassName())) {
                flywayProperties.setLocations(Collections.singletonList("classpath:db/migration/h2"));
            } else if (DatasourceDriver.POSTGRE_SQL_DRIVER.equals(dataSourceProperties.getDriverClassName())) {
                flywayProperties.setLocations(Collections.singletonList("classpath:db/migration/postgres"));
            } else {
                flywayProperties.setLocations(Collections.singletonList("classpath:db/migration/mysql"));
            }
        }
    }

    @Bean
    @Primary
    public FlywayProperties flywayProperties() {
        return flywayProperties;
    }
}
