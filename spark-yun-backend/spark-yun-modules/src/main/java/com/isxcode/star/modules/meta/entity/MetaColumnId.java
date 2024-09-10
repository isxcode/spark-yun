package com.isxcode.star.modules.meta.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaColumnId implements Serializable {

    private String datasourceId;

    private String tableName;

    private String columnName;
}
