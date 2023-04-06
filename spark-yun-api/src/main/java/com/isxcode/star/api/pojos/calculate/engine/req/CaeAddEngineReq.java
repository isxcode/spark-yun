package com.isxcode.star.api.pojos.calculate.engine.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CaeAddEngineReq {

  @NotEmpty(message = "引擎名称不能为空")
  private String name;

  private String comment;
}
