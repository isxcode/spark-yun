package com.isxcode.star.api.pojos.work.file.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WofFileListReq {

  @Schema(title = "作业唯一id", example = "sy_48c4304593ea4897b6af999e48685896")
  @NotEmpty(message = "作业id不能为空")
  private String workId;

  @Schema(title = "资源文件类型", example = "jar")
  private String type;
}
