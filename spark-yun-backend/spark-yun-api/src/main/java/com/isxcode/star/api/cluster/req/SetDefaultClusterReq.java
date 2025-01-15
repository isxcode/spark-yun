package com.isxcode.star.api.cluster.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SetDefaultClusterReq {

    @Schema(title = "计算引擎唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda")
    @NotEmpty(message = "集群id不能为空")
    private String clusterId;
}
