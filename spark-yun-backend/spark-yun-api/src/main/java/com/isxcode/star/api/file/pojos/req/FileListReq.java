package com.isxcode.star.api.file.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileListReq {

  @Schema(title = "文件名", example = "sy_48c4304593ea4897b6af999e48685896")
  private String fileName;

  @Schema(title = "资源文件类型", example = "lib")
  private String type;
}
