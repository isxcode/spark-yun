package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetDataSourceTablesReq {

	@Schema(title = "数据源唯一id", example = "sy_123456789")
	@NotEmpty(message = "作业id不能为空")
	private String dataSourceId;

  @Schema(title = "数据库表名", example = "SY")
  @NotEmpty(message = "作业id不能为空")
  private String tablePattern;
}
