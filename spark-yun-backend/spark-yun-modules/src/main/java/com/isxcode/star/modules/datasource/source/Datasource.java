package com.isxcode.star.modules.datasource.source;

import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.pojos.dto.QueryColumnDto;
import com.isxcode.star.api.datasource.pojos.dto.QueryTableDto;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.service.DatabaseDriverService;
import com.isxcode.star.modules.datasource.service.DriverShim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import static com.isxcode.star.common.config.CommonConfig.JPA_TENANT_MODE;
import static com.isxcode.star.modules.datasource.service.DatasourceService.ALL_EXIST_DRIVER;

@Slf4j
@RequiredArgsConstructor
public abstract class Datasource {

    private final DatabaseDriverService dataDriverService;

    private final IsxAppProperties isxAppProperties;

    private final AesUtils aesUtils;

    public abstract String getDataSourceType();

    public abstract String getDriverName();

    protected abstract List<QueryTableDto> queryTable(Connection connection, String database, String datasourceId,
        String tablePattern) throws SQLException;

    protected abstract List<QueryColumnDto> queryColumn(Connection connection, String database, String datasourceId,
        String tableName) throws SQLException;

    protected abstract Long getTableTotalSize(Connection connection, String database, String tableName)
        throws SQLException;

    protected abstract Long getTableTotalRows(Connection connection, String database, String tableName)
        throws SQLException;

    protected abstract Long getTableColumnCount(Connection connection, String database, String tableName)
        throws SQLException;

    public List<QueryTableDto> queryTable(DatasourceEntity datasourceEntity, String database, String tablePattern) {

        try {
            Connection connection = getConnection(datasourceEntity);
            return queryTable(connection, database, datasourceEntity.getId(), tablePattern);
        } catch (Exception e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public List<QueryColumnDto> queryColumn(DatasourceEntity datasourceEntity, String database, String datasourceId,
        String tableName) {

        try {
            Connection connection = getConnection(datasourceEntity);
            return queryColumn(connection, database, datasourceId, tableName);
        } catch (Exception e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public Long getTableTotalRows(DatasourceEntity datasourceEntity, String database, String tableName) {

        try {
            Connection connection = getConnection(datasourceEntity);
            return getTableTotalRows(connection, database, tableName);
        } catch (Exception e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public Long getTableTotalSize(DatasourceEntity datasourceEntity, String database, String tableName) {

        try {
            Connection connection = getConnection(datasourceEntity);
            return getTableTotalSize(connection, database, tableName);
        } catch (Exception e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public Long getTableColumnCount(DatasourceEntity datasourceEntity, String database, String tableName) {

        try {
            Connection connection = getConnection(datasourceEntity);
            return getTableColumnCount(connection, database, tableName);
        } catch (Exception e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public Connection getConnection(DatasourceEntity datasource) throws SQLException {

        // 判断驱动是否已经加载
        DriverShim driver = ALL_EXIST_DRIVER.get(datasource.getDriverId());
        if (driver == null) {

            JPA_TENANT_MODE.set(false);
            DatabaseDriverEntity driverEntity = dataDriverService.getDriver(datasource.getDriverId());
            JPA_TENANT_MODE.set(true);

            String driverPath = "TENANT_DRIVER".equals(driverEntity.getDriverType())
                ? driverEntity.getTenantId() + File.separator + driverEntity.getFileName()
                : "system" + File.separator + driverEntity.getFileName();

            // 先加载驱动到ALL_EXIST_DRIVER
            try {
                URL url = new File(PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                    + "jdbc" + File.separator + driverPath).toURI().toURL();
                ClassLoader driverClassLoader = new URLClassLoader(new URL[] {url});

                // 特殊逻辑判断，如果驱动是mysql5的使用
                String driverClassName = getDriverName();
                if (DatasourceType.MYSQL.equals(datasource.getDbType())) {
                    if (driverPath.contains("-5")) {
                        driverClassName = "com.mysql.jdbc.Driver";
                    }
                }

                Class<?> driverClass = driverClassLoader.loadClass(driverClassName);
                driver = new DriverShim((Driver) driverClass.newInstance());
                ALL_EXIST_DRIVER.put(datasource.getDriverId(), driver);
            } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException
                | InstantiationException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        Properties properties = new Properties();
        if (datasource.getUsername() != null) {
            properties.put("user", datasource.getUsername());
        }
        if (datasource.getPasswd() != null) {
            properties.put("password", aesUtils.decrypt(datasource.getPasswd()));
        }
        DriverManager.setLoginTimeout(500);
        return driver.connect(datasource.getJdbcUrl(), properties);
    }
}
