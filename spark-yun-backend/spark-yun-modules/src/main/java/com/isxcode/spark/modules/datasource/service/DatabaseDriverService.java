package com.isxcode.spark.modules.datasource.service;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.common.jpa.JpaTenantContext;
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

    public DatabaseDriverEntity getDriver(String driverId) {

        return databaseDriverRepository.findById(driverId).orElseThrow(() -> new IsxAppException("数据源驱动不存在"));
    }

    public DatabaseDriverEntity getVisibleDriver(String driverId) {

        return getDriver(driverId);
    }

    public DatabaseDriverEntity getShareVisibleDriver(String driverId) {

        return JpaTenantContext.joinShareData(() -> getVisibleDriver(driverId));
    }

    public DatabaseDriverEntity getAllVisibleDriver(String driverId) {

        return JpaTenantContext.joinAllData(() -> getVisibleDriver(driverId));
    }

    public String getDriverName(String driverId) {

        DatabaseDriverEntity databaseDriver = databaseDriverRepository.findById(driverId).orElse(null);
        return databaseDriver == null ? driverId : databaseDriver.getName();
    }
}
