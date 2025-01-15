package com.isxcode.star.modules.datasource.service;

import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.KafkaConfig;
import com.isxcode.star.api.datasource.dto.SecurityColumnDto;
import com.isxcode.star.backend.api.base.exceptions.WorkRunException;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.source.DataSourceFactory;
import com.isxcode.star.modules.datasource.source.Datasource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

    private final DatasourceRepository datasourceRepository;

    private final DataSourceFactory dataSourceFactory;

    public final static Map<String, DriverShim> ALL_EXIST_DRIVER = new ConcurrentHashMap<>();

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
            case DatasourceType.SQL_SERVER:
                return "select getdate() as nowtime";
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
}
