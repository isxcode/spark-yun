package com.isxcode.spark.api.secret.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddSecretReq {

    @Schema(title = "加密的key", example = "key")
    @NotEmpty(message = "加密的key不能为空")
    private String keyName;

    @Schema(title = "加密的值", example = "value")
    @NotEmpty(message = "加密的值不能为空")
    private String secretValue;

    @Schema(title = "备注", example = "备注内容")
    private String remark;
}
