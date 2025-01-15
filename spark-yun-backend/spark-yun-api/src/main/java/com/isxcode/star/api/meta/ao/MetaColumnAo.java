package com.isxcode.star.api.meta.ao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetaColumnAo {

    private String datasourceId;

    private String tableName;

    private String columnName;

    private String columnComment;

    private String columnType;

    private String customComment;

    private LocalDateTime lastModifiedDateTime;

    public MetaColumnAo(String datasourceId, String tableName, String columnName, String columnComment,
        String columnType, String customComment, LocalDateTime lastModifiedDateTime) {

        this.columnType = columnType;
        this.datasourceId = datasourceId;
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnComment = columnComment;
        this.customComment = customComment;
        this.lastModifiedDateTime = lastModifiedDateTime;
    }
}
