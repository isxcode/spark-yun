package com.isxcode.star.api.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMetaDto {

    @Schema(title = "字段列名")
    private String name;

    @Schema(title = "字段类型")
    private String type;

    @Schema(title = "是否为主键")
    private Boolean isPrimaryColumn;

    @Schema(title = "是否为非空键")
    private Boolean isNoNullColumn;

    @Schema(title = "字段长度")
    private Integer columnLength;
}
