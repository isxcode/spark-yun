package com.isxcode.star.api.datasource.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetDefaultDatabaseDriverReq {

    @Schema(description = "数据源类型", example = "MYSQL")
    @NotEmpty(message = "数据源类型不能为空")
    private String dbType;
}
