package com.isxcode.spark.api.tenant.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddTenantReq {

    @Schema(title = "租户名称", example = "中国大数据租户")
    @NotEmpty(message = "租户名称不能为空")
    private String name;

    @Schema(title = "租户描述", example = "超轻量级智能化大数据中心")
    private String remark;

    @Schema(title = "最大工作流数", example = "10")
    private Integer maxWorkflowNum;

    @Schema(title = "最大成员数", example = "10")
    private Integer maxMemberNum;

    @Schema(title = "管理员用户id", example = "sy_1234567890")
    @NotEmpty(message = "租户管理员不能为空")
    private String adminUserId;

    @Schema(title = "有效期开始时间", example = "2025-12-12 12:12:12")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validStartDateTime;

    @Schema(title = "有效期结束时间", example = "2025-12-12 12:12:12")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validEndDateTime;
}
