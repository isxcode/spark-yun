package com.isxcode.star.api.datasource.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeleteDatasourceReq {

    @Schema(description = "数据源唯一id", example = "sy_344c3d583fa344f7a2403b19c5a654dc")
    @NotEmpty(message = "数据源id不能为空")
    private String datasourceId;
}
