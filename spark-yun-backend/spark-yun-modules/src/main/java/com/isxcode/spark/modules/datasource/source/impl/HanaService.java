package com.isxcode.spark.modules.datasource.source.impl;

import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.api.datasource.constants.ColumnCode;
import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.datasource.dto.QueryColumnDto;
import com.isxcode.spark.api.datasource.dto.QueryTableDto;
import com.isxcode.spark.api.work.res.GetDataSourceDataRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.modules.datasource.service.DatabaseDriverService;
import com.isxcode.spark.modules.datasource.source.Datasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HanaService extends Datasource {

    public HanaService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.HANA_SAP;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.HANA_SAP_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) {
        return Collections.emptyList();
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) {
        return Collections.emptyList();
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) {
        return 0L;
    }

    @Override
    public Long getTableTotalRows(ConnectInfo connectInfo) {
        return 0L;
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) {
        return 0L;
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return "";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) {
        return null;
    }

    @Override
    public void refreshTableInfo(ConnectInfo connectInfo) {

    }

    @Override
    public String generateCreateTableSuffix(List<ColumnMetaDto> fromColumnList) {
        return "";
    }

    @Override
    public String convertColumnCode(ColumnMetaDto columnMeta) {
        String type = columnMeta.getType().toLowerCase();
        if (type.contains("(")) {
            type = type.substring(0, type.indexOf("("));
        }
        switch (type) {
            case "tinyint":
            case "smallint":
            case "integer":
            case "int":
                return ColumnCode.INT;
            case "bigint":
                return ColumnCode.BIGINT;
            case "real":
                return ColumnCode.FLOAT;
            case "double":
                return ColumnCode.DOUBLE;
            case "decimal":
            case "smalldecimal":
                return ColumnCode.DECIMAL;
            case "char":
            case "nchar":
                return ColumnCode.CHAR;
            case "varchar":
            case "nvarchar":
            case "alphanum":
                return ColumnCode.STRING;
            case "clob":
            case "nclob":
            case "text":
                return ColumnCode.TEXT;
            case "date":
                return ColumnCode.DATE;
            case "timestamp":
            case "seconddate":
                return ColumnCode.DATETIME;
            case "boolean":
                return ColumnCode.BOOLEAN;
            default:
                return ColumnCode.STRING;
        }
    }

    @Override
    public String convertColumnType(ColumnMetaDto columnMeta, String columnCode) {
        StringBuilder columnDef = new StringBuilder();
        columnDef.append(columnMeta.getName()).append(" ");
        switch (columnCode) {
            case ColumnCode.BOOLEAN:
                columnDef.append("BOOLEAN");
                break;
            case ColumnCode.INT:
                columnDef.append("INTEGER");
                break;
            case ColumnCode.BIGINT:
                columnDef.append("BIGINT");
                break;
            case ColumnCode.FLOAT:
                columnDef.append("REAL");
                break;
            case ColumnCode.DOUBLE:
                columnDef.append("DOUBLE");
                break;
            case ColumnCode.DECIMAL:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("DECIMAL(").append(columnMeta.getColumnLength()).append(",2)");
                } else {
                    columnDef.append("DECIMAL(10,2)");
                }
                break;
            case ColumnCode.CHAR:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("NCHAR(").append(columnMeta.getColumnLength()).append(")");
                } else {
                    columnDef.append("NCHAR(50)");
                }
                break;
            case ColumnCode.STRING:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("NVARCHAR(").append(columnMeta.getColumnLength()).append(")");
                } else {
                    columnDef.append("NVARCHAR(255)");
                }
                break;
            case ColumnCode.TEXT:
                columnDef.append("NCLOB");
                break;
            case ColumnCode.DATE:
                columnDef.append("DATE");
                break;
            case ColumnCode.DATETIME:
            case ColumnCode.TIMESTAMP:
                columnDef.append("TIMESTAMP");
                break;
            default:
                columnDef.append("NVARCHAR(255)");
                break;
        }
        if (columnMeta.getIsNoNullColumn() != null && columnMeta.getIsNoNullColumn()) {
            columnDef.append(" NOT NULL");
        }
        if (columnMeta.getIsPrimaryColumn() != null && columnMeta.getIsPrimaryColumn()) {
            columnDef.append(" PRIMARY KEY");
        }
        return columnDef.toString();
    }


    @Override
    public String convertToFlinkColumnType(String columnType) {

        if (columnType == null) {
            return "STRING";
        }

        String normalizedType = columnType.trim().toLowerCase();
        if (normalizedType.isEmpty()) {
            return "STRING";
        }

        normalizedType = normalizedType.replace("unsigned", "").replace("zerofill", "").trim();

        if (normalizedType.contains("array") || normalizedType.endsWith("[]")) {
            return "ARRAY<STRING>";
        }
        if (normalizedType.contains("map")) {
            return "MAP<STRING, STRING>";
        }

        if (normalizedType.contains("boolean") || normalizedType.contains("bool")
            || normalizedType.matches("bit\\s*\\(\\s*1\\s*\\)")) {
            return "BOOLEAN";
        }

        if (normalizedType.contains("tinyint") || normalizedType.contains("smallint")
            || normalizedType.contains("mediumint") || normalizedType.matches("(^|\\W)int(\\W|$)")
            || normalizedType.contains("integer") || normalizedType.contains("serial")) {
            return "INT";
        }
        if (normalizedType.contains("bigint") || normalizedType.contains("int8")
            || normalizedType.contains("bigserial")) {
            return "BIGINT";
        }

        if (normalizedType.contains("float") || normalizedType.contains("real")
            || normalizedType.contains("binary_float")) {
            return "FLOAT";
        }
        if (normalizedType.contains("double") || normalizedType.contains("binary_double")) {
            return "DOUBLE";
        }
        if (normalizedType.contains("decimal") || normalizedType.contains("numeric")
            || normalizedType.contains("number") || normalizedType.contains("money")) {
            return "DECIMAL(38, 18)";
        }

        if (normalizedType.matches("(^|\\W)date(\\W|$)")) {
            return "DATE";
        }
        if (normalizedType.contains("timestamp") || normalizedType.contains("datetime")
            || normalizedType.contains("smalldatetime") || normalizedType.matches("(^|\\W)time(\\W|$)")) {
            return "TIMESTAMP";
        }

        if (normalizedType.contains("blob") || normalizedType.contains("binary") || normalizedType.contains("varbinary")
            || normalizedType.contains("bytea") || normalizedType.contains("raw") || normalizedType.contains("image")) {
            return "BYTES";
        }

        return "STRING";
    }

}
