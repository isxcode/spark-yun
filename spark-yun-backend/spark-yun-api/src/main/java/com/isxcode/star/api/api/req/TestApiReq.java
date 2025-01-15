package com.isxcode.star.api.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
public class TestApiReq {

    @Schema(title = "API id", example = "sy_123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "post请求体", example = "")
    private Object requestBody;

    @Schema(title = "k:v 请求头", example = "{\"key\":\"value\"}")
    private Map<String, String> headerParams;
}
