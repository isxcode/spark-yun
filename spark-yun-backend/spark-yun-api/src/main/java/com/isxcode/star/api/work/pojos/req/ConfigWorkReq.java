package com.isxcode.star.api.work.pojos.req;

import com.isxcode.star.api.work.pojos.dto.ClusterConfig;
import com.isxcode.star.api.work.pojos.dto.CronConfig;
import com.isxcode.star.api.work.pojos.dto.SyncRule;
import com.isxcode.star.api.work.pojos.dto.SyncWorkConfig;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ConfigWorkReq {

	@Schema(title = "作业唯一id", example = "sy_4f07ab7b1fe54dab9be884e410c53af4")
	@NotEmpty(message = "workId不能为空")
	private String workId;

	@Schema(title = "数据源唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
	private String datasourceId;

	@Schema(title = "运行脚本", example = "show databases;")
	private String script;

	@Schema(title = "cron定时配置")
	private CronConfig cronConfig;

	@Schema(title = "cron定时配置")
	private SyncWorkConfig syncWorkConfig;

	@Schema(title = "集群配置")
	private ClusterConfig clusterConfig;

	@Schema(title = "数据同步规则")
	private SyncRule syncRule;
}
