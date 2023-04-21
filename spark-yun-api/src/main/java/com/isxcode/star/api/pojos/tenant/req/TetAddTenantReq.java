package com.isxcode.star.api.pojos.tenant.req;

import lombok.Data;

@Data
public class TetAddTenantReq {

  private String name;

  private String describe;

  private Integer maxWorkflowNum;

  private Integer maxMemberNum;
}
