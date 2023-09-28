package com.isxcode.star.api.work.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ConfigWorkReq {

	@Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
	@NotEmpty(message = "workId不能为空")
	private String workId;

	@Schema(title = "运行脚本", example = "show databases;")
	private String sqlScript;

	@Schema(title = "数据源唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
	private String datasourceId;

	@Schema(title = "计算引擎唯一id", example = "sy_354554267db34602896c35b4162fd4d8")
	private String clusterId;

  @Schema(title = "计算节点唯一id", example = "sy_354554267db34602896c35b4162fd4d8")
  private String clusterNodeId;

	@Schema(title = "spark的配置文件", example = "{\"spark.executor.memory\":\"1g\",\"spark.driver.memory\":\"1g\"}")
	private String sparkConfig;

  @Schema(title = "bash脚本语句", example = "cat /etc/hosts")
  private String bashScript;

	@Schema(title = "corn表达式", example = "0 0/3 * * * ?")
	private String corn;
}
