package com.isxcode.star.api.work.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GetSubmitLogReq {

    @Schema(description = "实例唯一id", example = "sy_12baf74d710c43a78858e547bf41a586")
    @NotEmpty(message = "实例id不能为空")
    private String instanceId;
}
