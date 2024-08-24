package com.isxcode.star.modules.datasource.source;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataSourceFactory {

    private final ApplicationContext applicationContext;

    public Datasource getDatasource(String datasourceType) {

        Optional<Datasource> datasourceOptional = applicationContext.getBeansOfType(Datasource.class).values().stream()
            .filter(agent -> agent.getDataSourceType().equals(datasourceType)).findFirst();

        if (!datasourceOptional.isPresent()) {
            throw new IsxAppException("数据源类型不支持");
        }

        return datasourceOptional.get();
    }
}
