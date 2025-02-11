package com.isxcode.star.modules.datasource.source;

import com.isxcode.star.api.datasource.constants.ColumnType;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.ConnectInfo;
import com.isxcode.star.api.datasource.dto.QueryColumnDto;
import com.isxcode.star.api.datasource.dto.QueryTableDto;
import com.isxcode.star.api.datasource.dto.SecurityColumnDto;
import com.isxcode.star.api.work.res.GetDataSourceDataRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.star.modules.datasource.service.DatabaseDriverService;
import com.isxcode.star.modules.datasource.service.DriverShim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public abstract List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException;

    public abstract List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException;

    public abstract Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException;

    public abstract Long getTableTotalRows(ConnectInfo connectInfo) throws IsxAppException;

    public abstract Long getTableColumnCount(ConnectInfo connectInfo) throws IsxAppException;

    public abstract String getPageSql(String sql) throws IsxAppException;

    public abstract GetDataSourceDataRes getTableData(ConnectInfo connectInfo) throws IsxAppException;

    public abstract void refreshTableInfo(ConnectInfo connectInfo) throws IsxAppException;

    public Connection getConnection(ConnectInfo connectInfo) throws IsxAppException {

        // 判断驱动是否已经加载
        DriverShim driver = ALL_EXIST_DRIVER.get(connectInfo.getDriverId());

        if (driver == null) {

            // 获取驱动
            JPA_TENANT_MODE.set(false);
            DatabaseDriverEntity driverEntity = dataDriverService.getDriver(connectInfo.getDriverId());
            JPA_TENANT_MODE.set(true);

            // 获取驱动路径
            String driverPath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                + "jdbc" + File.separator
                + ("TENANT_DRIVER".equals(driverEntity.getDriverType())
                    ? driverEntity.getTenantId() + File.separator + driverEntity.getFileName()
                    : "system" + File.separator + driverEntity.getFileName());

            // 如果docker部署，使用指定目录获取系统驱动
            if (isxAppProperties.isDockerMode()) {
                driverPath = "/var/lib/zhiqingyun-system/" + driverEntity.getFileName();
            }

            // 先加载驱动到ALL_EXIST_DRIVER
            URL url;
            try {
                url = new File(driverPath).toURI().toURL();
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
                throw new IsxAppException(e.getMessage());
            }
            ClassLoader driverClassLoader = new URLClassLoader(new URL[] {url});

            // 特殊逻辑判断，如果驱动是mysql5的使用
            String driverClassName = getDriverName();
            if (DatasourceType.MYSQL.equals(connectInfo.getDbType())) {
                if (driverPath.contains("-5")) {
                    driverClassName = "com.mysql.jdbc.Driver";
                }
            }

            // 加载驱动
            Class<?> driverClass;
            try {
                driverClass = driverClassLoader.loadClass(driverClassName);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
                throw new IsxAppException(e.getMessage());
            }
            try {
                driver = new DriverShim((Driver) driverClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new IsxAppException(e.getMessage());
            }
            ALL_EXIST_DRIVER.put(connectInfo.getDriverId(), driver);
        }

        // 配置账号密码
        Properties properties = new Properties();
        if (connectInfo.getUsername() != null) {
            properties.put("user", connectInfo.getUsername());
        }
        if (connectInfo.getPasswd() != null) {
            properties.put("password", aesUtils.decrypt(connectInfo.getPasswd()));
        }

        // 数据源连接超时时间设定，默认600秒，10分钟
        DriverManager.setLoginTimeout(connectInfo.getLoginTimeout() == null ? 600 : connectInfo.getLoginTimeout());
        try {
            return driver.connect(connectInfo.getJdbcUrl(), properties);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo, String getTableDataSql) throws IsxAppException {

        try (Connection connection = getConnection(connectInfo);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getTableDataSql);) {
            List<String> columns = new ArrayList<>();
            List<List<String>> rows = new ArrayList<>();

            // 封装表头
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(resultSet.getMetaData().getColumnName(i));
            }

            // 封装数据
            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(String.valueOf(resultSet.getObject(i)));
                }
                rows.add(row);
            }
            statement.close();
            connection.close();
            return GetDataSourceDataRes.builder().columns(columns).rows(rows).build();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public String parseDbName(String jdbcUrl) {

        String regex = "jdbc:\\w+://\\S+/(\\w+)";

        // sqlserver databaseName in jdbcUrl is different
        if (jdbcUrl.contains("jdbc:sqlserver://")) {
            if (jdbcUrl.toLowerCase().contains(";databasename=")) {
                regex = "databasename=([^;&]+)";
            }
            if (jdbcUrl.toLowerCase().contains(";database=")) {
                regex = "database=([^;&]+)";
            }
        } else if (jdbcUrl.contains("jdbc:h2:")) {
            return "PUBLIC";
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jdbcUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "default";
    }

    public void executeSql(ConnectInfo connectInfo, String sql) throws IsxAppException {

        try (Connection connection = getConnection(connectInfo)) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public void securityExecuteSql(ConnectInfo connectInfo, String securityExecuteSql,
        List<SecurityColumnDto> securityColumns) throws IsxAppException {

        try (Connection connection = getConnection(connectInfo);
            PreparedStatement statement = connection.prepareStatement(securityExecuteSql);) {
            for (int i = 0; i < securityColumns.size(); i++) {
                this.transAndSetParameter(statement, securityColumns.get(i), i);
            }
            statement.execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("提交失败," + e.getMessage());
        }
    }

    public ResultSet securityQuerySql(ConnectInfo connectInfo, String securityExecuteSql,
        List<SecurityColumnDto> securityColumns) throws IsxAppException {

        try {
            Connection connection = getConnection(connectInfo);
            PreparedStatement statement = connection.prepareStatement(securityExecuteSql);
            for (int i = 0; i < securityColumns.size(); i++) {
                this.transAndSetParameter(statement, securityColumns.get(i), i);
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public boolean tableIsExist(ConnectInfo connectInfo, String tableName) {

        try (Connection connection = getConnection(connectInfo);
            PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT 1 FROM " + tableName + " WHERE 1 = 0")) {
            preparedStatement.executeQuery();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("doesn't exist")) {
                return false;
            }
            log.error(e.getMessage(), e);
            throw new IsxAppException("判断表是否存在异常");
        }
    }

    public void transAndSetParameter(PreparedStatement statement, SecurityColumnDto securityColumnDto,
        int parameterIndex) throws SQLException {

        switch (securityColumnDto.getType()) {
            case ColumnType.STRING:
                if (securityColumnDto.getValue() == null) {
                    statement.setNull(parameterIndex + 1, java.sql.Types.VARCHAR);
                } else {
                    statement.setString(parameterIndex + 1, String.valueOf(securityColumnDto.getValue()));
                }
                break;
            case ColumnType.INT:
                if (securityColumnDto.getValue() == null) {
                    statement.setNull(parameterIndex + 1, Types.INTEGER);
                } else {
                    statement.setInt(parameterIndex + 1,
                        Integer.parseInt(String.valueOf(securityColumnDto.getValue())));
                }
                break;
            case ColumnType.DOUBLE:
                if (securityColumnDto.getValue() == null) {
                    statement.setNull(parameterIndex + 1, Types.DOUBLE);

                } else {
                    statement.setDouble(parameterIndex + 1,
                        Double.parseDouble(String.valueOf(securityColumnDto.getValue())));
                }
                break;
            case ColumnType.TIMESTAMP:
            case ColumnType.DATE:
            case ColumnType.DATE_TIME:
                if (securityColumnDto.getValue() == null) {
                    statement.setNull(parameterIndex + 1, Types.TIMESTAMP);
                } else {
                    statement.setTimestamp(parameterIndex + 1,
                        new Timestamp(Long.parseLong(String.valueOf(securityColumnDto.getValue()))));
                }
                break;
            case ColumnType.BIG_DECIMAL:
                if (securityColumnDto.getValue() == null) {
                    statement.setNull(parameterIndex + 1, Types.NUMERIC);
                } else {
                    statement.setBigDecimal(parameterIndex + 1,
                        new BigDecimal(String.valueOf(securityColumnDto.getValue())));
                }
                break;
            case ColumnType.BOOLEAN:
                if (securityColumnDto.getValue() == null) {
                    statement.setNull(parameterIndex + 1, Types.BOOLEAN);
                } else {
                    statement.setBoolean(parameterIndex + 1,
                        Boolean.parseBoolean(String.valueOf(securityColumnDto.getValue())));
                }
                break;
            default:
                throw new IsxAppException("字段类型不支持");
        }
    }

    public long securityGetTableCount(ConnectInfo connectInfo, String securityExecuteSql,
        List<SecurityColumnDto> securityColumns) {

        try (Connection connection = getConnection(connectInfo);
            PreparedStatement statement = connection.prepareStatement(securityExecuteSql);) {

            for (int i = 0; i < securityColumns.size(); i++) {
                this.transAndSetParameter(statement, securityColumns.get(i), i);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("提交失败," + e.getMessage());
        }
        throw new IsxAppException("查询总条数异常");
    }
}
