package com.isxcode.star.config;

import com.alibaba.druid.util.JdbcUtils;
import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.vendor.Database;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
@EnableConfigurationProperties({DataSourceProperties.class, JpaProperties.class, IsxAppProperties.class})
@ConditionalOnClass(JdbcUtils.class)
@RequiredArgsConstructor
public class JpaConfig {

    private final JpaProperties jpaProperties;

    private final DataSourceProperties dataSourceProperties;

    private final IsxAppProperties isxAppProperties;

    @PostConstruct
    public void changeJpaProperties() {

        if ("simple".equals(isxAppProperties.getConfigMode())) {
            if (DatasourceDriver.H2_DRIVER.equals(dataSourceProperties.getDriverClassName())) {
                jpaProperties.setDatabase(Database.H2);
            } else if (DatasourceDriver.POSTGRE_SQL_DRIVER.equals(dataSourceProperties.getDriverClassName())) {
                jpaProperties.setDatabase(Database.POSTGRESQL);
            } else {
                jpaProperties.setDatabase(Database.MYSQL);
            }
        }
    }

    @Bean
    @Primary
    public JpaProperties jpaProperties() {
        return jpaProperties;
    }
}
