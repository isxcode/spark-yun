package com.isxcode.spark.config;

import com.alibaba.druid.util.JdbcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
@EnableConfigurationProperties(JpaProperties.class)
@ConditionalOnClass(JdbcUtils.class)
@RequiredArgsConstructor
public class JpaConfig {

    private final JpaProperties jpaProperties;

    @Bean
    @Primary
    public JpaProperties jpaProperties() {
        return jpaProperties;
    }
}
