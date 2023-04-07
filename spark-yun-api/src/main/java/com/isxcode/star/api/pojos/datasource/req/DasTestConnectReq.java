package com.isxcode.star.api.pojos.datasource.req;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DasTestConnectReq {

  @Schema(title = "数据源唯一id",example = "sy_fd34e4a53db640f5943a4352c4d549b9")
  @NotEmpty(message = "数据源id不能为空")
  private String datasourceId;
}
