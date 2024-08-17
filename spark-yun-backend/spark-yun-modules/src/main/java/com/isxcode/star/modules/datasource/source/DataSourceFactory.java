package com.isxcode.star.modules.datasource.source;

import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataSourceFactory {

    private final Map<String, SourceService> sourceMap;

    public DataSourceFactory(ApplicationContext applicationContext) {

        sourceMap = applicationContext.getBeansOfType(SourceService.class).values().stream()
                .collect(Collectors.toMap(SourceService::getDataSourceType, action -> action));
    }

    public SourceService getDatasource(String dataSourceType) {

        return Optional.ofNullable(sourceMap.get(dataSourceType)).orElseThrow(() -> new RuntimeException("该数据源不支持"));
    }
}
