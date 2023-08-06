package com.isxcode.star.api.tenant.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TetAddTenantReq {

  @Schema(title = "租户名称", example = "中国大数据租户")
  @NotEmpty(message = "租户名称不能为空")
  private String name;

  @Schema(title = "租户描述", example = "这是中国超轻量级大数据平台")
  private String remark;

  @Schema(title = "最大工作流数", example = "10")
  private Integer maxWorkflowNum;

  @Schema(title = "最大成员数", example = "10")
  private Integer maxMemberNum;

  @Schema(title = "管理员用户id", example = "sy_1234567890")
  @NotEmpty(message = "租户管理员不能为空")
  private String adminUserId;
}
