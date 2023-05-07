package com.isxcode.star.api.pojos.calculate.engine.req;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CaeAddEngineReq {

  @Schema(title = "计算引擎名称", example = "至轻云内部计算引擎")
  @NotEmpty(message = "引擎名称不能为空")
  private String name;

  @Schema(title = "备注", example = "仅供内部使用")
  private String remark;
}
