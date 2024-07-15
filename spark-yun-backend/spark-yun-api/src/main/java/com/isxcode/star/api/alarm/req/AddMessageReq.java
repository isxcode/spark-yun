package com.isxcode.star.api.alarm.req;

import com.isxcode.star.api.alarm.dto.MessageConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddMessageReq {

    @Schema(title = "消息体名称", example = "系统邮箱")
    @NotEmpty(message = "消息体名称不能为空")
    private String name;

    @Schema(title = "备注", example = "仅供内部使用")
    private String remark;

    @Schema(title = "消息体类型", example = "")
    @NotEmpty(message = "消息体类型不能为空")
    private String msgType;

    @Schema(title = "消息体配置", example = "{}")
    @NotNull(message = "消息体配置不能为空")
    private MessageConfig messageConfig;

}
