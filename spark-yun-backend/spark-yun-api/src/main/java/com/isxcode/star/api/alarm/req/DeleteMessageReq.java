package com.isxcode.star.api.alarm.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteMessageReq {

    @Schema(title = "消息体id", example = "sy_123")
    @NotEmpty(message = "消息体id不能为空")
    private String id;
}
