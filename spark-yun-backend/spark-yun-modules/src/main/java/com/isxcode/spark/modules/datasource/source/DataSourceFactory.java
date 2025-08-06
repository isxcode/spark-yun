package com.isxcode.spark.modules.datasource.source;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataSourceFactory {

    private final ApplicationContext applicationContext;

    public Datasource getDatasource(String datasourceType) {

        return applicationContext.getBeansOfType(Datasource.class).values().stream()
            .filter(agent -> agent.getDataSourceType().equals(datasourceType)).findFirst()
            .orElseThrow(() -> new IsxAppException("数据源类型不支持"));
    }
}
