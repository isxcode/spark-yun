package com.isxcode.star.backend.api.base.pojos;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BasePageRequest {

  @Schema(title = "第几页", example = "0")
  @NotNull(message = "page不能为空")
  private Integer page;

  @Schema(title = "每页条数", example = "10")
  @NotNull(message = "pageSize不能为空")
  private Integer pageSize;

  @Schema(title = "搜索内容", example = "至轻云")
  private String searchKeyWord;
}
