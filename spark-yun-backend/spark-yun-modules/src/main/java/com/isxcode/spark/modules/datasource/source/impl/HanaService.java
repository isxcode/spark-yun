package com.isxcode.spark.modules.datasource.source.impl;

import com.isxcode.spark.api.datasource.constants.DatasourceDriver;
import com.isxcode.spark.api.datasource.constants.ColumnCode;
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
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Fetch;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
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
    public String generateLimitSql(String sql, Integer limit) throws JSQLParserException {

        if (isShowQueryStatement(sql)) {
            return sql;
        }

        net.sf.jsqlparser.statement.Statement statement = CCJSqlParserUtil.parse(sql);
        if (!(statement instanceof Select)) {
            throw new IllegalArgumentException("该Sql不是查询语句");
        }

        Select select = (Select) statement;
        PlainSelect plainSelect = select.getPlainSelect();

        if (plainSelect.getOrderByElements() == null || plainSelect.getOrderByElements().isEmpty()) {
            plainSelect.addOrderByElements(new OrderByElement().withExpression(new LongValue(1)).withAsc(true));
        }

        Fetch fetch = new Fetch();
        fetch.setFetchParamFirst(true);
        fetch.setExpression(new LongValue(limit));
        plainSelect.setFetch(fetch);

        return statement.toString();
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
    public String generateDataModelSql(ConnectInfo connectInfo, List<DataModelColumnAo> modelColumnList,
        DataModelEntity dataModelEntity) throws IsxAppException {
        throw new RuntimeException("数据源暂不支持，请联系管理员");
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
    public String getCreateTableFormat() {
        return "CREATE TABLE %s (%s) %s %s";
    }

    @Override
    public String getCreateTableSuffix(List<ColumnMetaDto> fromColumnList) {
        return "";
    }

    @Override
    public String getCreateTableOptionalSuffix(List<ColumnMetaDto> fromColumnList) {
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

}
