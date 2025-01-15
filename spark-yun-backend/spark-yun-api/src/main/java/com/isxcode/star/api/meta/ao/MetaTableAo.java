package com.isxcode.star.api.meta.ao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetaTableAo {

    private String datasourceId;

    private String tableName;

    private String tableComment;

    private String customComment;

    private LocalDateTime lastModifiedDateTime;

    public MetaTableAo(String datasourceId, String tableName, String tableComment, String customComment,
        LocalDateTime lastModifiedDateTime) {
        this.datasourceId = datasourceId;
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.customComment = customComment;
        this.lastModifiedDateTime = lastModifiedDateTime;
    }
}
