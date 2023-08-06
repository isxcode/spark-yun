package com.isxcode.star.api.work.pojos.req;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WokRenameWorkReq {

  @NotEmpty(message = "作业id不能为空")
  private String workId;

  @NotEmpty(message = "作业名称不能为空")
  private String workName;
}
