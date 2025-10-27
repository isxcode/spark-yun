package com.isxcode.spark.config;

import com.isxcode.spark.common.locker.LockerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 每次重启项目，需要清空锁表
 */
@Slf4j
@Configuration
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
@RequiredArgsConstructor
public class ClearLockerConfig {

    private final LockerRepository lockerRepository;

    @Bean
    public void clearLocker() {

        lockerRepository.deleteAll();
    }
}
