package com.isxcode.star.api.cluster.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class TestAgentReq {

    @Schema(title = "节点服务器hostname", example = "192.168.115.103")
    @NotEmpty(message = "host不能为空")
    private String host;

    @Schema(title = "节点服务器ssh端口号", example = "22")
    private String port;

    @Schema(title = "节点服务器用户名", example = "ispong")
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @Schema(title = "节点服务器密码", example = "ispong123")
    @NotEmpty(message = "密码或者令牌不能为空")
    private String passwd;
}
