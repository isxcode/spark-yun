package com.isxcode.star.api.license.pojos.req;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LicenseReq {

  /** 企业logo. */
  private String logo;

  /** 企业名称. */
  private String companyName;

  /** 许可证编码. */
  private String code;

  /** 许可证描述. */
  private String remark;

  /** 开始时间. */
  private LocalDateTime startDateTime;

  /** 结束时间. */
  private LocalDateTime endDateTime;

  /** 最大成员数. */
  private Integer maxMemberNum;

  /** 最大租户数. */
  private Integer maxTenantNum;

  /** 最大作业流数. */
  private Integer maxWorkflowNum;
}
