package com.isxcode.star.api.cluster.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeleteClusterReq {

  @Schema(title = "计算引擎唯一id", example = "sy_b0288cadb2ab4325ae519ff329a95cda")
  @NotEmpty(message = "集群id不能为空")
  private String engineId;
}
