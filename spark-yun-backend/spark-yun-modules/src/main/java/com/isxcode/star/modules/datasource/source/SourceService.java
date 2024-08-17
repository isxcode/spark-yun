package com.isxcode.star.modules.datasource.source;

import com.isxcode.star.modules.datasource.entity.DatasourceEntity;

import java.util.List;

public interface SourceService {

    String getDataSourceType();

    String getDriverName();

    List<String> queryTables(DatasourceEntity datasourceEntity);
}
