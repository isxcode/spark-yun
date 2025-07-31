package com.isxcode.star.modules.work.service;

import com.isxcode.star.api.datasource.dto.ColumnMetaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncWorkService {

    public List<String> tables(DatabaseMetaData metaData, String catalog, String schema, String tablePattern)
        throws SQLException {
        List<String> list = new ArrayList<>();
        ResultSet tables = metaData.getTables(catalog, schema, "%", new String[] {"TABLE"});
        Pattern pattern = Pattern.compile(tablePattern);
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            if (Strings.isBlank(tablePattern) || pattern.matcher(tableName).matches()) {
                list.add(tableName);
            }
        }
        return list;
    }

    public List<String> views(DatabaseMetaData metaData, String catalog, String schema, String tablePattern)
        throws SQLException {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(tablePattern);
        ResultSet views = metaData.getTables(catalog, schema, "%", new String[] {"VIEW"});
        while (views.next()) {
            String tableName = views.getString("TABLE_NAME");
            if (Strings.isBlank(tablePattern) || pattern.matcher(tableName).matches()) {
                list.add(tableName);
            }
        }
        return list;
    }

    public List<ColumnMetaDto> columns(DatabaseMetaData metaData, String catalog, String schema, String table)
        throws SQLException {

        // 获取主键信息
        Set<String> primaryKeys = getPrimaryKeys(metaData, catalog, schema, table);

        List<ColumnMetaDto> list = new ArrayList<>();
        ResultSet columns = metaData.getColumns(catalog, schema, table, null);
        while (columns.next()) {
            ColumnMetaDto columnMeta = new ColumnMetaDto();

            // 获取字段名
            columnMeta.setName(columns.getString("COLUMN_NAME"));

            // 获取字段类型
            String type = columns.getString("TYPE_NAME");
            if ("VARCHAR".equals(type)) {
                type += "(" + columns.getInt("COLUMN_SIZE") + ")";
            }
            columnMeta.setType(type);

            // 是否为主键
            columnMeta.setIsPrimaryColumn(primaryKeys.contains(columnMeta.getName()));

            // 是否为必填字段
            columnMeta.setIsNoNullColumn(columns.getInt("NULLABLE") == DatabaseMetaData.columnNoNulls);

            // 字段长度
            columnMeta.setColumnLength(columns.getInt("COLUMN_SIZE"));

            list.add(columnMeta);
        }
        return list;
    }

    private Set<String> getPrimaryKeys(DatabaseMetaData metaData, String catalog, String schema, String table)
        throws SQLException {

        Set<String> primaryKeys = new HashSet<>();
        ResultSet primaryKeyResultSet = metaData.getPrimaryKeys(catalog, schema, table);
        while (primaryKeyResultSet.next()) {
            String columnName = primaryKeyResultSet.getString("COLUMN_NAME");
            primaryKeys.add(columnName);
        }
        return primaryKeys;
    }

    public Map<String, String> transform(String dataBase, String catalog, String schema) {
        Map<String, String> result = new HashMap<>();

        if (dataBase != null) {
            if (catalog == null && schema == null) {
                // 未支持的数据库，全量查询
                return result;
            } else if (schema != null) {
                schema = dataBase;
            } else {
                catalog = dataBase;
            }
        }

        result.put("catalog", catalog);
        result.put("schema", schema);

        return result;
    }
}
