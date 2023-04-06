package com.isxcode.star.api.pojos.engine.node.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EnoQueryNodeReq {

  @NotEmpty(message = "engineId不能为空")
  private String engineId;

  private Integer page;

  private Integer pageSize;
}
