package com.isxcode.star.api.alarm.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckMessageReq {

    @Schema(title = "消息体id", example = "sy_123")
    @NotEmpty(message = "消息体id不能为空")
    private String id;

    @Schema(title = "发送的人", example = "sy_123")
    @NotEmpty(message = "发送人不能为空")
    private String receiver;

    @Schema(title = "发送内容", example = "hello")
    @NotEmpty(message = "内容不能为空")
    private String content;
}
