package com.isxcode.star.api.view.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddViewReq {

    @Schema(title = "大屏名称", example = "大屏测试")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
