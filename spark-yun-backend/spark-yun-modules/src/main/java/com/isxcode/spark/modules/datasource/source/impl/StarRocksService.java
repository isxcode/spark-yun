package com.isxcode.spark.modules.datasource.source.impl;

import com.isxcode.spark.api.datasource.constants.ColumnCode;
import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.api.datasource.dto.ColumnMetaDto;
import com.isxcode.spark.api.datasource.constants.DatasourceType;
import com.isxcode.spark.api.datasource.dto.ConnectInfo;
import com.isxcode.spark.api.datasource.dto.QueryColumnDto;
import com.isxcode.spark.api.datasource.dto.QueryTableDto;
import com.isxcode.spark.api.model.ao.DataModelColumnAo;
import com.isxcode.spark.api.work.res.GetDataSourceDataRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.modules.datasource.service.DatabaseDriverService;
import com.isxcode.spark.modules.datasource.source.Datasource;
import com.isxcode.spark.modules.model.entity.DataModelEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StarRocksService extends Datasource {

    public StarRocksService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties,
        AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.STAR_ROCKS;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.STAR_ROCKS_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) throws IsxAppException {
        throw new RuntimeException("数据源暂不支持，请联系管理员");
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) throws IsxAppException {
        throw new RuntimeException("数据源暂不支持，请联系管理员");
    }

    @Override
    public String generateDataModelSql(ConnectInfo connectInfo, List<DataModelColumnAo> modelColumnList,
        DataModelEntity dataModelEntity) throws IsxAppException {
        throw new RuntimeException("数据源暂不支持，请联系管理员");
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) throws IsxAppException {
        return 0L;
    }

    @Override
    public Long getTableTotalRows(ConnectInfo connectInfo) throws IsxAppException {
        return 0L;
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) throws IsxAppException {
        return 0L;
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return sql + " LIMIT '${page}' OFFSET '${pageSize}'";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) throws IsxAppException {
        return null;
    }

    @Override
    public void refreshTableInfo(ConnectInfo connectInfo) throws IsxAppException {

    }


    @Override
    public String getCreateTableFormat() {
        return "CREATE TABLE %s (%s) %s %s";
    }

    @Override
    public String getCreateTableSuffix(List<ColumnMetaDto> fromColumnList) {
        return "ENGINE=OLAP";
    }

    @Override
    public String getCreateTableOptionalSuffix(List<ColumnMetaDto> fromColumnList) {
        // StarRocks 需要指定分布键
        String firstColumn = fromColumnList.isEmpty() ? "id" : fromColumnList.get(0).getName();
        return "DISTRIBUTED BY HASH(" + firstColumn + ") BUCKETS 1";
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
            case "int":
                return ColumnCode.INT;
            case "bigint":
            case "largeint":
                return ColumnCode.BIGINT;
            case "float":
                return ColumnCode.FLOAT;
            case "double":
                return ColumnCode.DOUBLE;
            case "decimal":
                return ColumnCode.DECIMAL;
            case "char":
                return ColumnCode.CHAR;
            case "varchar":
            case "string":
                return ColumnCode.STRING;
            case "text":
                return ColumnCode.TEXT;
            case "date":
                return ColumnCode.DATE;
            case "datetime":
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
                columnDef.append("INT");
                break;
            case ColumnCode.BIGINT:
                columnDef.append("BIGINT");
                break;
            case ColumnCode.FLOAT:
                columnDef.append("FLOAT");
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
                    columnDef.append("CHAR(").append(columnMeta.getColumnLength()).append(")");
                } else {
                    columnDef.append("CHAR(50)");
                }
                break;
            case ColumnCode.STRING:
                if (columnMeta.getColumnLength() != null && columnMeta.getColumnLength() > 0) {
                    columnDef.append("VARCHAR(").append(columnMeta.getColumnLength()).append(")");
                } else {
                    columnDef.append("VARCHAR(255)");
                }
                break;
            case ColumnCode.TEXT:
                columnDef.append("STRING");
                break;
            case ColumnCode.DATE:
                columnDef.append("DATE");
                break;
            case ColumnCode.DATETIME:
            case ColumnCode.TIMESTAMP:
                columnDef.append("DATETIME");
                break;
            default:
                columnDef.append("VARCHAR(255)");
                break;
        }
        return columnDef.toString();
    }

}
