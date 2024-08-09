package com.isxcode.star.modules.work.service.biz;

import com.isxcode.star.api.datasource.pojos.dto.ColumnMetaDto;
import com.isxcode.star.api.work.pojos.req.*;
import com.isxcode.star.api.work.pojos.res.*;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.work.service.SyncWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SyncWorkBizService {

    private final DatasourceService datasourceService;

    private final SyncWorkService syncWorkService;

    private final DatasourceRepository datasourceRepository;

    private final AesUtils aesUtils;

    public GetDataSourceTablesRes getDataSourceTables(GetDataSourceTablesReq getDataSourceTablesReq) throws Exception {

        Connection connection = syncWorkService.getConnection(getDataSourceTablesReq.getDataSourceId(), null, null);
        Map<String, String> transform = getTransform(connection, getDataSourceTablesReq.getTablePattern());
        List<String> tables = syncWorkService.tables(connection.getMetaData(), transform.get("catalog"),
            transform.get("schema"), transform.get("tableName"));
        List<String> views = syncWorkService.views(connection.getMetaData(), transform.get("catalog"),
            transform.get("schema"), transform.get("tableName"));
        tables.addAll(views);
        connection.close();
        return GetDataSourceTablesRes.builder().tables(tables).build();
    }

    public GetDataSourceColumnsRes getDataSourceColumns(GetDataSourceColumnsReq getDataSourceColumnsReq)
        throws Exception {

        Connection connection = syncWorkService.getConnection(getDataSourceColumnsReq.getDataSourceId(), null, null);
        Map<String, String> transform = getTransform(connection, getDataSourceColumnsReq.getTableName());
        List<ColumnMetaDto> columns = syncWorkService.columns(connection.getMetaData(), transform.get("catalog"),
            transform.get("schema"), transform.get("tableName"));
        connection.close();
        return GetDataSourceColumnsRes.builder().columns(columns).build();
    }

    public GetDataSourceDataRes getDataSourceData(GetDataSourceDataReq getDataSourceDataReq) throws Exception {

        Optional<DatasourceEntity> datasourceEntityOptional =
            datasourceRepository.findById(getDataSourceDataReq.getDataSourceId());
        if (!datasourceEntityOptional.isPresent()) {
            throw new IsxAppException("数据源异常，请联系开发者");
        }

        Connection connection = datasourceService.getDbConnection(datasourceEntityOptional.get());

        Statement statement = connection.createStatement();
        String dataPreviewSql = syncWorkService.getDataPreviewSql(datasourceEntityOptional.get().getDbType(),
            getDataSourceDataReq.getTableName());
        ResultSet resultSet = statement.executeQuery(dataPreviewSql);
        List<String> columns = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        // 封装表头
        int columnCount = resultSet.getMetaData().getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(resultSet.getMetaData().getColumnName(i));
        }

        // 封装数据，最多200条
        while (resultSet.next() && rows.size() < 200) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(String.valueOf(resultSet.getObject(i)));
            }
            rows.add(row);
        }
        statement.close();
        connection.close();
        return GetDataSourceDataRes.builder().columns(columns).rows(rows).build();
    }

    public GetCreateTableSqlRes getCreateTableSql(GetCreateTableSqlReq getCreateTableSqlReq) throws Exception {
        Connection connection = syncWorkService.getConnection(getCreateTableSqlReq.getDataSourceId(), null, null);
        Map<String, String> transform = getTransform(connection, getCreateTableSqlReq.getTableName());
        ResultSet columns = connection.getMetaData().getColumns(transform.get("catalog"), transform.get("schema"),
            transform.get("tableName"), null);
        String sql = String.join(" ", "CREATE TABLE", transform.get("tableName"), "(");
        while (columns.next()) {
            sql = String.join(" ", sql, "\n", columns.getString("COLUMN_NAME"), "String,");
        }
        connection.close();
        return GetCreateTableSqlRes.builder().sql(sql.substring(0, sql.length() - 1) + "\n)").build();
    }

    private Map<String, String> getTransform(Connection connection, String tableName) {
        String dataBase = null;
        if (tableName.contains(".")) {
            dataBase = tableName.split("\\.")[0];
            tableName = tableName.split("\\.")[1];
        }

        String catalog = getCatalogOrSchema(connection, true);
        String schema = getCatalogOrSchema(connection, false);

        Map<String, String> transform = syncWorkService.transform(dataBase, catalog, schema);
        transform.put("tableName", tableName);
        return transform;
    }

    private String getCatalogOrSchema(Connection connection, boolean isCatalog) {
        try {
            if (isCatalog) {
                return connection.getCatalog();
            } else {
                return connection.getSchema();
            }
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            return null;
        }
    }
}
