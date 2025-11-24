package com.isxcode.spark.api.meta.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetColumnLinageReq {

    @Schema(title = "数据源id", example = "")
    @NotEmpty(message = "dbId不能为空")
    private String dbId;

    @Schema(title = "表名", example = "")
    @NotEmpty(message = "tableName不能为空")
    private String tableName;

    @Schema(title = "字段名", example = "")
    @NotEmpty(message = "colName不能为空")
    private String columnName;

    @Schema(title = "上游PARENT/下游SON", example = "")
    @NotEmpty(message = "lineageType不能为空")
    private String lineageType;
}
