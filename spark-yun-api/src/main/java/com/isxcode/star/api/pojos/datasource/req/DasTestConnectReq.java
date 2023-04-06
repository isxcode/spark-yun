package com.isxcode.star.api.pojos.datasource.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DasTestConnectReq {

  @NotEmpty(message = "数据源id不能为空")
  private String datasourceId;
}
