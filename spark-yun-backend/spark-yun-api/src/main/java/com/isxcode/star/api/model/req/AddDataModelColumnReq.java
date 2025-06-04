package com.isxcode.star.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddDataModelColumnReq {

    @Schema(title = "数据模型名称", example = "123")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "字段", example = "123")
    @NotEmpty(message = "columnName不能为空")
    private String columnName;

    @Schema(title = "数据模型id", example = "123")
    @NotEmpty(message = "modelId不能为空")
    private String modelId;

    @Schema(title = "字段标准id", example = "123")
    @NotEmpty(message = "columnFormatId不能为空")
    private String columnFormatId;

    @Schema(title = "备注", example = "123")
    private String remark;
}
