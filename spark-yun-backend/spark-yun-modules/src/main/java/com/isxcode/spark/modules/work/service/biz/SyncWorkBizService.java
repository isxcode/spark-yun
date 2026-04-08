package com.isxcode.spark.modules.work.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.isxcode.spark.api.api.constants.ApiType;
import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.datasource.dto.TableMetaInfo;
import com.isxcode.spark.api.work.req.*;
import com.isxcode.spark.api.work.res.*;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.common.utils.http.HttpUtils;
import com.isxcode.spark.modules.datasource.entity.DatasourceEntity;
import com.isxcode.spark.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.datasource.source.DataSourceFactory;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.meta.entity.MetaColumnEntity;
import com.isxcode.spark.modules.meta.repository.MetaColumnRepository;
import com.isxcode.spark.modules.work.service.SyncWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SyncWorkBizService {

    private final DatasourceService datasourceService;

    private final SyncWorkService syncWorkService;

    private final DataSourceFactory dataSourceFactory;

    private final DatasourceMapper datasourceMapper;

    private final MetaColumnRepository metaColumnRepository;

    public GetDataSourceTablesRes getDataSourceTables(GetDataSourceTablesReq getDataSourceTablesReq) throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getDataSourceTablesReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        Connection connection;
        try {
            connection = datasource.getConnection(connectInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("【" + datasourceEntity.getName() + "】连接异常，请检查数据源");
        }
        TableMetaInfo tableMetaInfo = getTableMetaInfo(connection, getDataSourceTablesReq.getTablePattern());
        List<String> tables = syncWorkService.tables(connection.getMetaData(), tableMetaInfo.getCatalog(),
            tableMetaInfo.getSchema(), tableMetaInfo.getTableName());

        // 开启视图开关再查询
        if (getDataSourceTablesReq.getIsListViews() == null || getDataSourceTablesReq.getIsListViews()) {
            List<String> views = syncWorkService.views(connection.getMetaData(), tableMetaInfo.getCatalog(),
                tableMetaInfo.getSchema(), tableMetaInfo.getTableName());
            tables.addAll(views);
        }
        connection.close();
        return GetDataSourceTablesRes.builder().tables(tables).build();
    }

    public GetDataSourceColumnsRes getDataSourceColumns(GetDataSourceColumnsReq getDataSourceColumnsReq)
        throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getDataSourceColumnsReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        Connection connection;
        try {
            connection = datasource.getConnection(connectInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("【" + datasourceEntity.getName() + "】连接异常，请检查数据源");
        }
        TableMetaInfo tableMetaInfo = getTableMetaInfo(connection, getDataSourceColumnsReq.getTableName());
        List<ColumnMetaDto> columns = syncWorkService.columns(connection.getMetaData(), tableMetaInfo.getCatalog(),
            tableMetaInfo.getSchema(), tableMetaInfo.getTableName());
        connection.close();

        // 给字段加注释
        columns.forEach(column -> {
            Optional<MetaColumnEntity> columnEntityOptional =
                metaColumnRepository.findByDatasourceIdAndTableNameAndColumnName(datasourceEntity.getId(),
                    getDataSourceColumnsReq.getTableName(), column.getName());
            columnEntityOptional
                .ifPresent(metaColumnEntity -> column.setColumnComment(metaColumnEntity.getColumnComment()));
        });
        return GetDataSourceColumnsRes.builder().columns(columns).build();
    }

    public GetDataSourceDataRes getDataSourceData(GetDataSourceDataReq getDataSourceDataReq) throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getDataSourceDataReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(datasourceEntity.getDbType());
        connectInfo.setTableName(getDataSourceDataReq.getTableName());
        connectInfo.setRowNumber("200");
        return datasource.getTableData(connectInfo);
    }

    public GetCreateTableSqlRes getCreateTableSql(GetCreateTableSqlReq getCreateTableSqlReq) throws Exception {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(getCreateTableSqlReq.getDataSourceId());
        ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
        Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
        Connection connection = datasource.getConnection(connectInfo);
        TableMetaInfo tableMetaInfo = getTableMetaInfo(connection, getCreateTableSqlReq.getTableName());
        ResultSet columns = connection.getMetaData().getColumns(tableMetaInfo.getCatalog(), tableMetaInfo.getSchema(),
            tableMetaInfo.getTableName(), null);
        String sql = String.join(" ", "CREATE TABLE", tableMetaInfo.getTableName(), "(");
        while (columns.next()) {
            sql = String.join(" ", sql, "\n", columns.getString("COLUMN_NAME"), "String,");
        }
        connection.close();
        return GetCreateTableSqlRes.builder().sql(sql.substring(0, sql.length() - 1) + "\n)").build();
    }

    private TableMetaInfo getTableMetaInfo(Connection connection, String tableName) {
        String catalog = null;
        String schema = null;
        String productName = null;
        String userName = null;
        String finalTableName = tableName == null ? null : tableName.trim();

        try {
            catalog = connection.getCatalog();
        } catch (SQLException | AbstractMethodError e) {
            log.debug(e.getMessage(), e);
        }
        try {
            schema = connection.getSchema();
        } catch (SQLException | AbstractMethodError e) {
            log.debug(e.getMessage(), e);
        }
        try {
            productName = connection.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
        }
        try {
            userName = connection.getMetaData().getUserName();
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
        }

        boolean canSplit = finalTableName != null && !finalTableName.trim().isEmpty() && !finalTableName.contains("^")
            && !finalTableName.contains("*") && !finalTableName.contains("%") && !finalTableName.contains("(")
            && !finalTableName.contains(")") && !finalTableName.contains("|") && !finalTableName.contains("[")
            && !finalTableName.contains("]") && finalTableName.contains(".");

        if (canSplit) {
            String[] split = finalTableName.split("\\.");
            if (split.length == 2) {
                // schema.table 或 catalog.table：优先填 schema，若 schema 为空且有 catalog 则填 catalog
                if ((schema == null || schema.trim().isEmpty()) && !(catalog == null || catalog.trim().isEmpty())) {
                    catalog = split[0];
                } else {
                    schema = split[0];
                }
                finalTableName = split[1];
            } else if (split.length == 3) {
                // catalog.schema.table
                catalog = split[0];
                schema = split[1];
                finalTableName = split[2];
            }
        }

        boolean isOracle = productName != null && productName.toLowerCase(Locale.ROOT).contains("oracle");
        if (isOracle) {
            // Oracle 使用 schema，不使用 catalog
            catalog = null;
            if (schema == null || schema.trim().isEmpty()) {
                if (userName != null && !userName.trim().isEmpty()) {
                    schema = userName.contains("@") ? userName.substring(0, userName.indexOf("@")) : userName;
                }
            }
            if (schema != null && !schema.trim().isEmpty()) {
                schema = schema.toUpperCase(Locale.ROOT);
            }
            boolean canFormat = finalTableName != null && !finalTableName.trim().isEmpty()
                && !finalTableName.contains("^") && !finalTableName.contains("*") && !finalTableName.contains("%");
            if (canFormat) {
                finalTableName = finalTableName.toUpperCase(Locale.ROOT);
            }
        }

        return TableMetaInfo.builder().catalog(catalog).schema(schema).tableName(finalTableName).build();
    }

    public GetApiDataRes getApiDataRes(GetApiDataReq getApiDataReq) {

        // 判断合法行
        if (Strings.isEmpty(getApiDataReq.getRequestType())) {
            throw new IsxAppException("请求方式不能为空");
        }
        if (Strings.isEmpty(getApiDataReq.getRequestHttp())) {
            throw new IsxAppException("请求地址不能为空");
        }
        if (Strings.isEmpty(getApiDataReq.getJsonDataType())) {
            throw new IsxAppException("解析类型不能为空");
        }
        if (getApiDataReq.getTableColumn().isEmpty()) {
            throw new IsxAppException("解析字段不能为空");
        }

        // 获取分页信息
        int pageSize = getApiDataReq.getPageSize();
        int startPage = getApiDataReq.getStartPage();

        // 封装请求头
        Map<String, String> requestHeader = getApiDataReq.getRequestHeader() == null ? new HashMap<>()
            : new HashMap<>(getApiDataReq.getRequestHeader());

        // 返回体
        String responseObj;
        try {
            if (ApiType.GET.equalsIgnoreCase(getApiDataReq.getRequestType())) {

                // 替换分页信息
                String requestUrl = getApiDataReq.getRequestHttp().replace("${page}", String.valueOf(startPage))
                    .replace("${pageSize}", String.valueOf(pageSize));

                // get请求返回
                responseObj = HttpUtils.doGet(requestUrl, null, String.class);
            } else if (ApiType.POST.equalsIgnoreCase(getApiDataReq.getRequestType())) {

                // 替换分页信息
                String requestBody = getApiDataReq.getRequestBody();
                if (requestBody != null) {
                    requestBody = requestBody.replace("\"${page}\"", String.valueOf(startPage))
                        .replace("\"${pageSize}\"", String.valueOf(pageSize));
                }

                responseObj = HttpUtils.doPost(getApiDataReq.getRequestHttp(), requestHeader,
                    JSON.parseObject(requestBody), String.class);
            } else {
                throw new IsxAppException("请求方式仅支持POST/GET");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("请求接口异常: " + e.getMessage());
        }

        // 解析数据
        List<List<String>> rows = new ArrayList<>();
        if ("OBJECT".equalsIgnoreCase(getApiDataReq.getJsonDataType())) {
            List<String> singleRow = new ArrayList<>();
            // 解析对象数据
            getApiDataReq.getTableColumn().forEach(column -> {
                Object value = JSONPath.extract(JSON.toJSONString(responseObj), column.getJsonPath());
                singleRow.add(String.valueOf(value));
            });
            rows.add(singleRow);
        } else if ("LIST".equalsIgnoreCase(getApiDataReq.getJsonDataType())) {
            // 解析数组数据
            Object extract = JSONPath.extract(responseObj, getApiDataReq.getNodeRootJsonPath());
            JSONArray resultJsonArray = JSON.parseArray(JSON.toJSONString(extract));
            for (Object metaData : resultJsonArray) {
                List<String> singleRow = new ArrayList<>();
                getApiDataReq.getTableColumn().forEach(column -> {
                    Object value = JSONPath.extract(JSON.toJSONString(metaData), column.getJsonPath());
                    singleRow.add(String.valueOf(value));
                });
                rows.add(singleRow);
            }
        } else {
            throw new IsxAppException("解析类型仅支持对象和数组");
        }

        List<String> columns = new ArrayList<>();
        getApiDataReq.getTableColumn().forEach(column -> columns.add(column.getCode()));

        return GetApiDataRes.builder().columns(columns).rows(rows).build();
    }

}
