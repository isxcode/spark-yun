package com.isxcode.star.api.real.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class QueryColumnPathReq {

    @Schema(title = "json", example = "{}")
    @NotEmpty(message = "jsonStr不能为空")
    private String jsonStr;

    @Schema(title = "如果是数组对象，则需要传递", example = "{}")
    private String rootPath;
}
