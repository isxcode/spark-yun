package com.isxcode.star.api.pojos.tenant.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TetQueryTenantRes {

  private String id;

  private String name;

  private String usedMemberNum;

  private String maxMemberNum;

  private String usedWorkflowNum;

  private String maxWorkflowNum;

  private String remark;

  private String status;

  private String checkDateTime;
}
