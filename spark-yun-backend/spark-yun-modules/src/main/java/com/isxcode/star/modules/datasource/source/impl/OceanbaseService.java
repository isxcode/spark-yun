package com.isxcode.star.modules.datasource.source.impl;

import com.isxcode.star.api.datasource.constants.DatasourceDriver;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.source.SourceService;

import java.util.Collections;
import java.util.List;

public class OceanbaseService implements SourceService {

    @Override
    public String getDataSourceType() {
        return DatasourceType.OCEANBASE;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.OCEAN_BASE_DRIVER;
    }

    @Override
    public List<String> queryTables(DatasourceEntity datasourceEntity) {
        return Collections.emptyList();
    }
}
