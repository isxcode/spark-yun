package com.isxcode.star.api.workflow.req;

import com.isxcode.star.api.work.dto.CronConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ConfigWorkflowSettingReq {

    @Schema(title = "工作流id", example = "123")
    @NotEmpty(message = "工作流id不能为空")
    private String workflowId;

    @Schema(title = "cron定时配置")
    private CronConfig cronConfig;

    @Schema(title = "告警")
    private List<String> alarmList;

    @Schema(title = "是否启动外部调用", example = "OFF关闭/ON开启")
    @NotEmpty(message = "调用状态invokeStatus不能为空")
    private String invokeStatus;

    @Schema(title = "调用地址", example = "curl xxx")
    private String invokeUrl;
}
