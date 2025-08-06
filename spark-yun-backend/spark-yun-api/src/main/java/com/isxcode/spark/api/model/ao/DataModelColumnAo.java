package com.isxcode.spark.api.model.ao;

import lombok.Data;


@Data
public class DataModelColumnAo {

    private String id;

    private String name;

    private String columnName;

    private String remark;

    private String columnFormatName;

    private String columnFormatId;

    private String columnTypeCode;

    private String columnType;

    private String linkColumnType;

    private String isNull;

    private String isDuplicate;

    private String isPartition;

    private String isPrimary;

    private String defaultValue;

    public DataModelColumnAo(String id, String name, String columnName, String remark, String columnFormatId,
        String columnFormatName, String columnTypeCode, String columnType, String linkColumnType, String isNull,
        String isDuplicate, String isPartition, String isPrimary, String defaultValue) {

        this.id = id;
        this.name = name;
        this.columnName = columnName;
        this.remark = remark;
        this.columnFormatId = columnFormatId;
        this.columnFormatName = columnFormatName;
        this.columnTypeCode = columnTypeCode;
        this.columnType = columnType;
        this.linkColumnType = linkColumnType;
        this.isNull = isNull;
        this.isDuplicate = isDuplicate;
        this.isPartition = isPartition;
        this.isPrimary = isPrimary;
        this.defaultValue = defaultValue;
    }
}

