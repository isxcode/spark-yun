package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddWorkReq {

	@Schema(title = "作业名称", example = "作业1")
	@NotEmpty(message = "作业名称不能为空")
	private String name;

	@Schema(title = "作业类型", example = "类型编码：" + "SPARK_SQL： spark sql执行作业" + "EXE_JDBC： jdbc执行作业"
			+ "QUERY_JDBC： jdbc查询作业" + "DATA_SYNC_JDBC： 数据同步作业" + "BASH： bash脚本作业" + "PYTHON： python脚本作业")
	@NotEmpty(message = "作业类型不能为空")
	private String workType;

	@Schema(title = "工作流唯一id", example = "sy_48c4304593ea4897b6af999e48685896")
	@NotEmpty(message = "工作流id不能为空")
	private String workflowId;

	@Schema(title = "备注", example = "星期一执行的作业")
	private String remark;
}
