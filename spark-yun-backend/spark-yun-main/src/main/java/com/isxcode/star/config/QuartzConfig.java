package com.isxcode.star.config;

import com.alibaba.druid.util.JdbcUtils;
import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
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
    }

    @Bean
    @Primary
    public QuartzProperties quartzProperties() {
        return quartzProperties;
    }
}
