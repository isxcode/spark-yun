package com.isxcode.star.api.pojos.calculate.engine.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CaeQueryEngineReq {

  @NotEmpty(message = "引擎名称不能为空")
  private Integer page;

  private Integer pageSize;

  private String searchContent;
}
