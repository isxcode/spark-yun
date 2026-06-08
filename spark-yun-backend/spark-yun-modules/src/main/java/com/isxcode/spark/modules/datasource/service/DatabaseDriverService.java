package com.isxcode.spark.modules.datasource.service;

import static com.isxcode.spark.common.jpa.JpaTenantContext.allData;
import static com.isxcode.spark.common.jpa.JpaTenantContext.noTenant;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.common.security.ContextHolder;
import com.isxcode.spark.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.spark.modules.datasource.repository.DatabaseDriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
@RequiredArgsConstructor
public class DatabaseDriverService {

    private final DatabaseDriverRepository databaseDriverRepository;

    public String getDriverName(String driverId) {

        DatabaseDriverEntity databaseDriver = databaseDriverRepository.findById(driverId).orElse(null);
        return databaseDriver == null ? driverId : databaseDriver.getName();
    }

    public DatabaseDriverEntity getDriver(String driverId) {

        return noTenant(() -> databaseDriverRepository.findById(driverId).orElseThrow(() -> new IsxAppException("数据源驱动不存在")));
    }
}
