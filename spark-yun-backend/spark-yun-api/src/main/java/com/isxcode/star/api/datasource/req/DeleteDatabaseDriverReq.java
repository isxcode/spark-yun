package com.isxcode.star.api.datasource.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteDatabaseDriverReq {

    @Schema(description = "数据源驱动唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc")
    @NotEmpty(message = "数据源驱动id不能为空")
    private String driverId;
}
