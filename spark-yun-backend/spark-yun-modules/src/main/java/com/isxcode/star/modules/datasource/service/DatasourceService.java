package com.isxcode.star.modules.datasource.service;

import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.pojos.dto.KafkaConfig;
import com.isxcode.star.api.datasource.pojos.dto.SecurityColumnDto;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.source.DataSourceFactory;
import com.isxcode.star.modules.datasource.source.Datasource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.SqlOrderBy;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.stereotype.Service;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.SqlNode;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

    private final IsxAppProperties isxAppProperties;

    private final DatasourceRepository datasourceRepository;

    private final DatabaseDriverService dataDriverService;

    private final DataSourceFactory dataSourceFactory;

    /**
     * 所有的驱动. driverId driver
     */
    public final static Map<String, DriverShim> ALL_EXIST_DRIVER = new ConcurrentHashMap<>();

    private final AesUtils aesUtils;

    public String getDriverClass(String datasourceType) {

        Datasource factoryDatasource = dataSourceFactory.getDatasource(datasourceType);
        return factoryDatasource.getDriverName();
    }

    public DatasourceEntity getDatasource(String datasourceId) {

        return datasourceRepository.findById(datasourceId).orElseThrow(() -> new IsxAppException("数据源不存在"));
    }

    public String getDatasourceName(String datasourceId) {

        DatasourceEntity datasource = datasourceRepository.findById(datasourceId).orElse(null);
        return datasource == null ? datasourceId : datasource.getName();
    }


    public String genDefaultSql(String datasourceId) {

        if (StringUtils.isEmpty(datasourceId)) {
            return "show databases";
        }

        DatasourceEntity datasource = getDatasource(datasourceId);

        switch (datasource.getDbType()) {
            case DatasourceType.HANA_SAP:
                return "SELECT TABLE_NAME FROM SYS.TABLES;";
            default:
                return "show databases";
        }
    }

    public boolean isQueryStatement(String sql) {

        SqlParser parser = SqlParser.create(sql);
        try {

            String sqlUpper = sql.trim().toUpperCase();
            if (sqlUpper.startsWith("SHOW TABLES") || sqlUpper.startsWith("SHOW DATABASES")) {
                return true;
            }

            SqlNode sqlNode = parser.parseQuery();
            return sqlNode.getKind() == SqlKind.SELECT || sqlNode.getKind() == SqlKind.ORDER_BY;
        } catch (SqlParseException e) {
            log.error(e.getMessage(), e);
            throw new WorkRunException(e.getMessage());
        }
    }

    public boolean checkSqlValid(String sql) {

        SqlParser parser = SqlParser.create(sql);
        try {
            parser.parseQuery(sql);
            return true;
        } catch (SqlParseException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public String parseDbName(String jdbcUrl) {

        Pattern pattern = Pattern.compile("jdbc:\\w+://\\S+/(\\w+)");
        Matcher matcher = pattern.matcher(jdbcUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "default";
    }

    /**
     * 解析sql select * from table where a = '${value1}' and b = ${value2} 获取sql中的参数顺序 List
     * [value1,value2]
     */
    public List<SecurityColumnDto> transSecurityColumns(String sql) {

        List<SecurityColumnDto> securityColumnList = new ArrayList<>();

        // 使用正则截取${}中的字符
        String pattern = "'\\$\\{(?!UPDATE_COLUMN\\b)([^}]+)\\}'";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(sql);
        int columnIndex = 10;
        while (matcher.find()) {
            String name = matcher.group(1);
            securityColumnList.add(SecurityColumnDto.builder().name(columnIndex + "." + name).build());
            columnIndex++;
        }
        return securityColumnList;
    }

    /**
     * 解析sql，将 select * from table where a = '${value1}' and b = ${value2} 转成 select * from table where
     * a = ? and b = ?
     */
    public String transSecuritySql(String sql) {

        return sql.replaceAll("'\\$\\{(?!UPDATE_COLUMN\\b)([^}]+)\\}'", "?");
    }

    public void checkKafka(KafkaConfig kafkaConfig) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        if (kafkaConfig.getProperties() != null) {
            properties.putAll(kafkaConfig.getProperties());
        }
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        properties.put(AdminClientConfig.RETRIES_CONFIG, 0);
        properties.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 3000);

        try (AdminClient adminClient = AdminClient.create(properties)) {
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            Set<String> strings = listTopicsResult.names().get();
            if (!strings.contains(kafkaConfig.getTopic())) {
                throw new RuntimeException("topic不存在");
            }
        }
    }

    public Set<String> queryKafkaTopic(KafkaConfig kafkaConfig) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        if (kafkaConfig.getProperties() != null) {
            properties.putAll(kafkaConfig.getProperties());
        }
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        properties.put(AdminClientConfig.RETRIES_CONFIG, 0);
        properties.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 3000);

        try (AdminClient adminClient = AdminClient.create(properties)) {
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            return listTopicsResult.names().get();
        }
    }

    /**
     * 判断sql是否有limit限制.
     */
    public boolean hasLimit(String sql) {

        String sqlUpper = sql.trim().toUpperCase();
        if (sqlUpper.startsWith("SHOW TABLES") || sqlUpper.startsWith("SHOW DATABASES")) {
            return true;
        }

        SqlParser parser = SqlParser.create(sql);
        try {
            SqlNode sqlNode = parser.parseStmt();
            if (sqlNode instanceof SqlSelect) {
                SqlSelect select = (SqlSelect) sqlNode;
                return select.getFetch() != null;
            } else if (sqlNode instanceof SqlOrderBy) {
                SqlOrderBy orderBy = (SqlOrderBy) sqlNode;
                return orderBy.fetch != null;
            } else {
                return false;
            }
        } catch (SqlParseException e) {
            log.error(e.getMessage(), e);
            throw new WorkRunException(e.getMessage());
        }
    }

    /**
     * 判断sql是否有where.
     */
    public boolean hasWhere(String sql) {

        SqlParser parser = SqlParser.create(sql);
        try {
            SqlNode sqlNode = parser.parseStmt();
            if (sqlNode instanceof SqlSelect) {
                SqlSelect select = (SqlSelect) sqlNode;
                return select.getWhere() != null;
            } else {
                return false;
            }
        } catch (SqlParseException e) {
            log.error(e.getMessage(), e);
            throw new WorkRunException(e.getMessage());
        }
    }

    /**
     * 生成sql的limit sql.
     */
    public String getSqlLimitSql(String dbType, Boolean hasWhere) {

        switch (dbType) {
            case DatasourceType.ORACLE:
                return hasWhere ? " AND ROWNUM <= 200" : " WHERE ROWNUM <= 200";
            default:
                return " limit 200";
        }
    }
}
