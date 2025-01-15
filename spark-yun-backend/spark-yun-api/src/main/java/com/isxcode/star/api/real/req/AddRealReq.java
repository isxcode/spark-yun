package com.isxcode.star.api.real.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddRealReq {

    @Schema(title = "实时作业名称", example = "测试作业")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "集群id", example = "sy_123")
    @NotEmpty(message = "clusterId不能为空")
    private String clusterId;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
