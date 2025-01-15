package com.isxcode.star.api.real.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class QueryTopicReq {

    @Schema(title = "数据源id", example = "sy_123")
    @NotEmpty(message = "datasourceId不能为空")
    private String datasourceId;
}
