package com.isxcode.star.api.pojos.engine.node.req;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EnoQueryNodeReq {

  @Schema(title = "计算引擎唯一id", example = "sy_e4d80a6b561d47afa81504e93054e8e8")
  @NotEmpty(message = "calculateEngineId不能为空")
  private String calculateEngineId;

  @Schema(title = "第几页", example = "0")
  @NotNull(message = "page不能为空")
  private Integer page;

  @Schema(title = "每页条数", example = "10")
  @NotNull(message = "pageSize不能为空")
  private Integer pageSize;

  @Schema(title = "搜索内容", example = "至轻云")
  private String searchContent;
}
