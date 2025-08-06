package com.isxcode.spark.api.datasource.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateDatabaseDriverRemarkReq {

    @Schema(description = "驱动id", example = "123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(description = "备注", example = "123")
    private String remark;
}
