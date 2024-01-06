package com.isxcode.star.api.form.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddFormReq {

	@Schema(title = "表单名称", example = "测试表单")
	@NotEmpty(message = "name不能为空")
	private String name;

	@Schema(title = "数据源id", example = "1234567")
	@NotEmpty(message = "datasourceId不能为空")
	private String datasourceId;

	@Schema(title = "表单基于的主表", example = "table1")
	@NotEmpty(message = "mainTable不能为空")
	private String mainTable;

	@Schema(title = "备注", example = "备注123")
	private String remark;
}
