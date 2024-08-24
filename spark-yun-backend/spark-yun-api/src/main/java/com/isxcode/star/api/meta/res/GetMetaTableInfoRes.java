package com.isxcode.star.api.meta.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMetaTableInfoRes {

    private String datasourceId;

    private String tableName;

    private String tableComment;

    private Long columnCount;

    private Long totalSize;

    private String totalSizeStr;

    private Long totalRows;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime refreshDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime collectDateTime;
}
