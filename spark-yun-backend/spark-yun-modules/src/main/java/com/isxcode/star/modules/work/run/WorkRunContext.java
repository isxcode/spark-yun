package com.isxcode.star.modules.work.run;

import com.isxcode.star.api.work.pojos.dto.ClusterConfig;
import com.isxcode.star.api.work.pojos.dto.JarJobConfig;
import com.isxcode.star.api.work.pojos.dto.SyncRule;
import com.isxcode.star.api.work.pojos.dto.SyncWorkConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 作业运行上下文.
 */
@Data
@Builder
public class WorkRunContext {

	/**
	 * 作业实例id.
	 */
	private String instanceId;

	/**
	 * 数据源.
	 */
	private String datasourceId;

	/**
	 * 计算集群信息.
	 */
	private ClusterConfig clusterConfig;

	/**
	 * 同步作业信息.
	 */
	private SyncWorkConfig syncWorkConfig;

	/**
	 * 同步规则.
	 */
	private SyncRule syncRule;

	/**
	 * 脚本.
	 */
	private String script;

	/**
	 * 租户id.
	 */
	private String tenantId;

	/**
	 * 用户id.
	 */
	private String userId;

	/**
	 * 作业id.
	 */
	private String workId;

	/**
	 * 作业名称.
	 */
	private String workName;

	/**
	 * 作业类型.
	 */
	private String workType;

	/**
	 * 版本id.
	 */
	private String versionId;

	/**
	 * 日志构造器.
	 */
	private StringBuilder logBuilder;

	/**
	 * udf启用状态.
	 */
	private Boolean udfStatus;

	/**
	 * 用户自定义jar的配置
	 */
	private JarJobConfig jarJobConfig;

	/**
	 * 自定义函数配置.
	 */
	private List<String> funcConfig;

	/**
	 * 依赖配置.
	 */
	private List<String> libConfig;

}
