package com.isxcode.star.api.alarm.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddAlarmReq {

    @Schema(title = "告警名称", example = "系统告警")
    @NotEmpty(message = "告警名称不能为空")
    private String name;

    @Schema(title = "备注", example = "仅供内部使用")
    private String remark;

    @Schema(title = "告警类型", example = "WORK/WORKFLOW")
    @NotEmpty(message = "告警类型不能为空")
    private String alarmType;

    @Schema(title = "告警事件", example = "RUN_SUCCESS/RUN_FAIL")
    @NotEmpty(message = "告警事件不能为空")
    private String alarmEvent;

    @Schema(title = "消息体", example = "sy_123")
    @NotEmpty(message = "消息体不能为空")
    private String msgId;

    @Schema(title = "通知的人", example = "sy_123")
    @NotEmpty(message = "通知的人不能为空")
    private List<String> receiverList;

    @Schema(title = "告警模版", example = "告警内容")
    @NotNull(message = "告警模版不能为空")
    private String alarmTemplate;
}
