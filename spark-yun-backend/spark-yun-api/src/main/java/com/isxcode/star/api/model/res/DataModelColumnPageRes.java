package com.isxcode.star.api.model.res;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class DataModelColumnPageRes {

    private String id;

    private String name;

    private String columnName;

    private String remark;

    private String columnFormatName;

    private String columnTypeCode;

    private String columnType;

    private String isNull;

    private String isDuplicate;

    private String isPartition;

    private String isPrimary;

    private String defaultValue;
}
