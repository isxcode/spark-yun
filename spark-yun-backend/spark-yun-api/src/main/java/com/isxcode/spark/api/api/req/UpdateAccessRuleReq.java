package com.isxcode.spark.api.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateAccessRuleReq {

    @Schema(title = "黑白名单id", example = "sy_123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "规则名称", example = "测试规则")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "规则类型", example = "WHITELIST / BLACKLIST")
    @NotEmpty(message = "ruleType不能为空")
    private String ruleType;

    @Schema(title = "IP地址，多个用换行分隔，支持正则", example = "192.168.1.1\n192.168.1.*")
    @NotEmpty(message = "ipAddress不能为空")
    private String ipAddress;

    @Schema(title = "备注", example = "备注123")
    private String remark;
}
