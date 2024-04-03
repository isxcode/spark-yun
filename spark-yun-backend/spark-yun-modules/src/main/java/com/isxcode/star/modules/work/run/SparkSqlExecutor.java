package com.isxcode.star.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.agent.pojos.req.PluginReq;
import com.isxcode.star.api.agent.pojos.req.SparkSubmit;
import com.isxcode.star.api.agent.pojos.req.YagExecuteWorkReq;
import com.isxcode.star.api.agent.pojos.res.YagGetLogRes;
import com.isxcode.star.api.api.constants.PathConstants;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.api.work.pojos.res.RunWorkRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.pojos.BaseResponse;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.locker.Locker;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.common.utils.http.HttpUrlUtils;
import com.isxcode.star.common.utils.http.HttpUtils;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.file.entity.FileEntity;
import com.isxcode.star.modules.file.repository.FileRepository;
import com.isxcode.star.modules.func.entity.FuncEntity;
import com.isxcode.star.modules.func.mapper.FuncMapper;
import com.isxcode.star.modules.func.repository.FuncRepository;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.utils.ssh.SshUtils.scpJar;

@Service
@Slf4j
public class SparkSqlExecutor extends WorkExecutor {

	private final WorkInstanceRepository workInstanceRepository;

	private final ClusterRepository clusterRepository;

	private final ClusterNodeRepository clusterNodeRepository;

	private final WorkRepository workRepository;

	private final WorkConfigRepository workConfigRepository;

	private final Locker locker;

	private final HttpUrlUtils httpUrlUtils;

	private final FuncRepository funcRepository;

	private final FuncMapper funcMapper;

	private final ClusterNodeMapper clusterNodeMapper;

	private final AesUtils aesUtils;

	private final IsxAppProperties isxAppProperties;

	private final FileRepository fileRepository;

	private final DatasourceService datasourceService;

	public SparkSqlExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
			ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
			WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker,
			HttpUrlUtils httpUrlUtils, FuncRepository funcRepository, FuncMapper funcMapper,
			ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, IsxAppProperties isxAppProperties,
			FileRepository fileRepository, DatasourceService datasourceService) {

		super(workInstanceRepository, workflowInstanceRepository);
		this.workInstanceRepository = workInstanceRepository;
		this.clusterRepository = clusterRepository;
		this.clusterNodeRepository = clusterNodeRepository;
		this.workRepository = workRepository;
		this.workConfigRepository = workConfigRepository;
		this.locker = locker;
		this.httpUrlUtils = httpUrlUtils;
		this.funcRepository = funcRepository;
		this.funcMapper = funcMapper;
		this.clusterNodeMapper = clusterNodeMapper;
		this.aesUtils = aesUtils;
		this.isxAppProperties = isxAppProperties;
		this.fileRepository = fileRepository;
		this.datasourceService = datasourceService;
	}

	@Override
	protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

		// 获取日志构造器
		StringBuilder logBuilder = workRunContext.getLogBuilder();

		// 判断执行脚本是否为空
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测脚本内容 \n");
		if (Strings.isEmpty(workRunContext.getScript())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "检测脚本失败 : SQL内容为空不能执行  \n");
		}

		// 检测计算集群是否存在
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
		if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎未配置  \n");
		}

		// 检查计算集群是否存在
		Optional<ClusterEntity> calculateEngineEntityOptional = clusterRepository
				.findById(workRunContext.getClusterConfig().getClusterId());
		if (!calculateEngineEntityOptional.isPresent()) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在  \n");
		}

		// 检测集群中是否有合法节点
		List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
				.findAllByClusterIdAndStatus(calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
		if (allEngineNodes.isEmpty()) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
		}

		// 节点选择随机数
		ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【")
				.append(engineNode.getName()).append("】\n");
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
		workInstance = updateInstance(workInstance, logBuilder);

		// 开始构建作业
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业  \n");
		YagExecuteWorkReq executeReq = new YagExecuteWorkReq();

		// 开始构造SparkSubmit
		SparkSubmit sparkSubmit = SparkSubmit.builder().verbose(true)
				.mainClass("com.isxcode.star.plugin.query.sql.Execute").appResource("spark-query-sql-plugin.jar")
				.conf(genSparkSubmitConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

		// 开始构造PluginReq
		PluginReq pluginReq = PluginReq.builder().sql(workRunContext.getScript()).limit(200)
				.sparkConfig(genSparkConfig(workRunContext.getClusterConfig().getSparkConfig())).build();

		// 导入自定义函数
		ScpFileEngineNodeDto scpFileEngineNodeDto = clusterNodeMapper
				.engineNodeEntityToScpFileEngineNodeDto(engineNode);
		scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
		String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
				+ File.separator + TENANT_ID.get();
		if (workRunContext.getFuncConfig() != null) {
			List<FuncEntity> allFunc = funcRepository.findAllById(workRunContext.getFuncConfig());
			allFunc.forEach(e -> {
				try {
					scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getFileId(),
							engineNode.getAgentHomePath() + File.separator + "zhiqingyun-agent" + File.separator
									+ "file" + File.separator + e.getFileId() + ".jar");
				} catch (JSchException | SftpException | InterruptedException | IOException ex) {
					throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "jar文件上传失败\n");
				}
			});
			pluginReq.setFuncInfoList(funcMapper.funcEntityListToFuncInfoList(allFunc));
			executeReq.setFuncConfig(funcMapper.funcEntityListToFuncInfoList(allFunc));
		}

		// 上传依赖到制定节点路径
		if (workRunContext.getLibConfig() != null) {
			List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
			libFile.forEach(e -> {
				try {
					scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getId(),
							engineNode.getAgentHomePath() + File.separator + "zhiqingyun-agent" + File.separator
									+ "file" + File.separator + e.getId() + ".jar");
				} catch (JSchException | SftpException | InterruptedException | IOException ex) {
					throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "jar文件上传失败\n");
				}
			});
			executeReq.setLibConfig(workRunContext.getLibConfig());
		}

		// 解析db
		if (StringUtils.isNotBlank(workRunContext.getDatasourceId())) {
			DatasourceEntity datasource = datasourceService.getDatasource(workRunContext.getDatasourceId());
			try {
				String database = datasourceService.parseDbName(datasource.getJdbcUrl());
				if (!Strings.isEmpty(database)) {
					pluginReq.setDatabase(database);
				}
			} catch (IsxAppException e) {
				throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + e.getMsg() + "\n");
			}
		}

		// 如果数据库id不为空,则替换hive的metastore url
		if (!Strings.isEmpty(workRunContext.getDatasourceId())) {
			pluginReq.getSparkConfig().put("hive.metastore.uris", datasource.getMetastoreUris());
		}

		// 开始构造executeReq
		executeReq.setSparkSubmit(sparkSubmit);
		executeReq.setPluginReq(pluginReq);
		executeReq.setAgentHomePath(engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME);
    if(engineNode.getInstallSparkLocal()) {
      executeReq.setSparkHomePath(engineNode.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + PathConstants.SPARK_MIN_HOME);
    } else {
      executeReq.setSparkHomePath(engineNode.getSparkHomePath());
    }
		executeReq.setAgentType(calculateEngineEntityOptional.get().getClusterType());

		// 构建作业完成，并打印作业配置信息
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
		workRunContext.getClusterConfig().getSparkConfig().forEach((k, v) -> logBuilder.append(LocalDateTime.now())
				.append(WorkLog.SUCCESS_INFO).append(k).append(":").append(v).append(" \n"));
		logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业  \n");
		workInstance = updateInstance(workInstance, logBuilder);

		// 开始提交作业
		BaseResponse<?> baseResponse;

		// 加锁，必须等待作业提交成功后才能中止
		Integer lock = locker.lock("REQUEST_" + workInstance.getId());
		RunWorkRes submitWorkRes;
		try {
			baseResponse = HttpUtils.doPost(
					httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), "/yag/executeWork"),
					executeReq, BaseResponse.class);
			log.debug("获取远程提交作业日志:{}", baseResponse.toString());
			if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
				throw new WorkRunException(
						LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
			}
			// 解析返回对象,获取appId
			if (baseResponse.getData() == null) {
				throw new WorkRunException(
						LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + baseResponse.getMsg() + "\n");
			}
			submitWorkRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
					.append(submitWorkRes.getAppId()).append("\n");
			workInstance.setSparkStarRes(JSON.toJSONString(submitWorkRes));
			workInstance = updateInstance(workInstance, logBuilder);
		} catch (IOException | HttpServerErrorException | ResourceAccessException e) {
			throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
		} finally {
			locker.unlock(lock);
		}

		// 提交作业成功后，开始循环判断状态
		while (true) {

			// 获取作业状态并保存
			Map<String, String> paramsMap = new HashMap<>();
			paramsMap.put("appId", submitWorkRes.getAppId());
			paramsMap.put("agentType", calculateEngineEntityOptional.get().getClusterType());
			paramsMap.put("sparkHomePath", engineNode.getSparkHomePath());
			baseResponse = HttpUtils.doGet(
					httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), "/yag/getStatus"),
					paramsMap, null, BaseResponse.class);
			log.debug("获取远程获取状态日志:{}", baseResponse.toString());

			if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
				throw new WorkRunException(
						LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + baseResponse.getMsg() + "\n");
			}

			// 解析返回状态，并保存
			RunWorkRes workStatusRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), RunWorkRes.class);
			workInstance.setSparkStarRes(JSON.toJSONString(workStatusRes));
			logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
					.append(workStatusRes.getAppStatus()).append("\n");
			workInstance = updateInstance(workInstance, logBuilder);

			// 如果状态是运行中，更新日志，继续执行
			List<String> runningStatus = Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING");
			if (runningStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					throw new WorkRunException(
							LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
				}
			} else {
				// 运行结束逻辑

				// 如果是中止，直接退出
				if ("KILLED".equals(workStatusRes.getAppStatus().toUpperCase())
						|| "TERMINATING".equals(workStatusRes.getAppStatus().toUpperCase())) {
					throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止" + "\n");
				}

				// 获取日志并保存
				Map<String, String> paramsMap2 = new HashMap<>();
				paramsMap2.put("appId", submitWorkRes.getAppId());
				paramsMap2.put("agentType", calculateEngineEntityOptional.get().getClusterType());
				paramsMap2.put("sparkHomePath", engineNode.getSparkHomePath());
				baseResponse = HttpUtils.doGet(
						httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), "/yag/getLog"),
						paramsMap2, null, BaseResponse.class);
				log.debug("获取远程返回日志:{}", baseResponse.toString());

				if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
					throw new WorkRunException(
							LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + baseResponse.getMsg() + "\n");
				}

				// 解析日志并保存
				YagGetLogRes yagGetLogRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()),
						YagGetLogRes.class);
				logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
				if (yagGetLogRes != null) {
					workInstance.setYarnLog(yagGetLogRes.getLog());
				}
				workInstance = updateInstance(workInstance, logBuilder);

				// 如果运行成功，则保存返回数据
				List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
				if (successStatus.contains(workStatusRes.getAppStatus().toUpperCase())) {

					// 获取数据
					Map<String, String> paramsMap3 = new HashMap<>();
					paramsMap3.put("appId", submitWorkRes.getAppId());
					paramsMap3.put("agentType", calculateEngineEntityOptional.get().getClusterType());
					paramsMap3.put("sparkHomePath", engineNode.getSparkHomePath());
					baseResponse = HttpUtils.doGet(
							httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), "/yag/getData"),
							paramsMap3, null, BaseResponse.class);
					log.debug("获取远程返回数据:{}", baseResponse.toString());

					if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
						throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业数据异常 : "
								+ baseResponse.getErr() + "\n");
					}

					// 解析数据并保存
					workInstance.setResultData(JSON.toJSONString(baseResponse.getData()));
					logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功 \n");
					updateInstance(workInstance, logBuilder);
				} else {
					// 任务运行错误
					throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
				}

				// 运行结束，则退出死循环
				break;
			}
		}
	}

	@Override
	protected void abort(WorkInstanceEntity workInstance) {

		// 判断作业有没有提交成功
		locker.lock("REQUEST_" + workInstance.getId());
		try {
			workInstance = workInstanceRepository.findById(workInstance.getId()).get();
			if (!Strings.isEmpty(workInstance.getSparkStarRes())) {
				RunWorkRes wokRunWorkRes = JSON.parseObject(workInstance.getSparkStarRes(), RunWorkRes.class);
				if (!Strings.isEmpty(wokRunWorkRes.getAppId())) {
					// 关闭远程线程
					WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
					WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
					List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
							.findAllByClusterIdAndStatus(workConfig.getClusterConfig(), ClusterNodeStatus.RUNNING);
					if (allEngineNodes.isEmpty()) {
						throw new WorkRunException(
								LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
					}
					ClusterEntity cluster = clusterRepository.findById(workConfig.getClusterConfig()).get();

					// 节点选择随机数
					ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

					Map<String, String> paramsMap = new HashMap<>();
					paramsMap.put("appId", wokRunWorkRes.getAppId());
					paramsMap.put("agentType", cluster.getClusterType());
					paramsMap.put("sparkHomePath", engineNode.getSparkHomePath());
					BaseResponse<?> baseResponse = HttpUtils.doGet(
							httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), "/yag/stopJob"),
							paramsMap, null, BaseResponse.class);

					if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
						throw new IsxAppException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
					}
				} else {
					// 先杀死进程
					WORK_THREAD.get(workInstance.getId()).interrupt();
				}
			}
		} finally {
			locker.clearLock("REQUEST_" + workInstance.getId());
		}
	}

	/**
	 * 初始化spark作业提交配置.
	 */
	public Map<String, String> genSparkSubmitConfig(Map<String, String> sparkConfig) {

		// 过滤掉，前缀不包含spark.xxx的配置，spark submit中必须都是spark.xxx
		Map<String, String> sparkSubmitConfig = new HashMap<>();
		sparkConfig.forEach((k, v) -> {
			if (k.startsWith("spark")) {
				sparkSubmitConfig.put(k, v);
			}
		});
		return sparkSubmitConfig;
	}

	/**
	 * sparkConfig不能包含k8s的配置
	 */
	public Map<String, String> genSparkConfig(Map<String, String> sparkConfig) {

		// k8s的配置不能提交到作业中
		sparkConfig.remove("spark.kubernetes.driver.podTemplateFile");
		sparkConfig.remove("spark.kubernetes.executor.podTemplateFile");

		return sparkConfig;
	}
}
