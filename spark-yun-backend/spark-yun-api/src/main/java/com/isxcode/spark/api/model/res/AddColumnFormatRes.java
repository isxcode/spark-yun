package com.isxcode.spark.api.model.res;

import lombok.Data;

@Data
public class AddColumnFormatRes {

    private String id;

    private String name;

    private String columnTypeCode;

    private String columnType;

    private String columnRule;

    private String status;

    private String remark;

    private String isNull;

    private String isDuplicate;

    private String isPartition;

    private String isPrimary;

    private String defaultValue;
}
