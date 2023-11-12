package com.isxcode.star.modules.workflow.service;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.config.CommonConfig.USER_ID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.instance.constants.FlowInstanceStatus;
import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.instance.constants.InstanceType;
import com.isxcode.star.api.work.constants.SetMode;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkStatus;
import com.isxcode.star.api.work.pojos.dto.CronConfig;
import com.isxcode.star.api.work.pojos.req.GetWorkflowDefaultClusterReq;
import com.isxcode.star.api.work.pojos.res.GetWorkflowDefaultClusterRes;
import com.isxcode.star.api.workflow.constants.WorkflowStatus;
import com.isxcode.star.api.workflow.pojos.dto.WorkInstanceInfo;
import com.isxcode.star.api.workflow.pojos.req.*;
import com.isxcode.star.api.workflow.pojos.res.GetRunWorkInstancesRes;
import com.isxcode.star.api.workflow.pojos.res.GetWorkflowRes;
import com.isxcode.star.api.workflow.pojos.res.PageWorkflowRes;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.locker.Locker;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.cluster.service.ClusterService;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkExportInfo;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkExecutorFactory;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.workflow.entity.WorkflowConfigEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowExportInfo;
import com.isxcode.star.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.star.modules.workflow.mapper.WorkflowMapper;
import com.isxcode.star.modules.workflow.repository.WorkflowConfigRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowRepository;
import com.isxcode.star.modules.workflow.run.WorkflowRunEvent;
import com.isxcode.star.modules.workflow.run.WorkflowUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户模块接口的业务逻辑.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowBizService {

	private final WorkflowRepository workflowRepository;

	private final WorkflowMapper workflowMapper;

	private final WorkflowConfigRepository workflowConfigRepository;

	private final ApplicationEventPublisher eventPublisher;

	private final WorkflowInstanceRepository workflowInstanceRepository;

	private final WorkInstanceRepository workInstanceRepository;

	private final WorkRepository workRepository;

	private final WorkConfigRepository workConfigRepository;

	private final WorkExecutorFactory workExecutorFactory;

	private final WorkflowService workflowService;

	private final Locker locker;

	private final Executor sparkYunWorkThreadPool;

	private final ClusterRepository clusterRepository;

	private final ClusterService clusterService;

	public WorkflowEntity getWorkflowEntity(String workflowId) {

		Optional<WorkflowEntity> workflowEntityOptional = workflowRepository.findById(workflowId);
		if (!workflowEntityOptional.isPresent()) {
			throw new IsxAppException("作业流不存在");
		}
		return workflowEntityOptional.get();
	}

	public void addWorkflow(AddWorkflowReq wofAddWorkflowReq) {

		// 工作流唯一性
		Optional<WorkflowEntity> workflowByName = workflowRepository.findByName(wofAddWorkflowReq.getName());
		if (workflowByName.isPresent()) {
			throw new IsxAppException("作业流名称已重复，请重新输入");
		}

		// 初始化工作流配置
		WorkflowConfigEntity workflowConfig = new WorkflowConfigEntity();
		workflowConfig.setCronConfig(
				JSON.toJSONString(CronConfig.builder().setMode(SetMode.SIMPLE).type("ALL").enable(false).build()));
		workflowConfig = workflowConfigRepository.save(workflowConfig);

		// 工作流绑定配置
		WorkflowEntity workflow = workflowMapper.addWorkflowReqToWorkflowEntity(wofAddWorkflowReq);
		workflow.setStatus(WorkflowStatus.UN_AUTO);
		workflow.setConfigId(workflowConfig.getId());
		workflowRepository.save(workflow);
	}

	public void updateWorkflow(UpdateWorkflowReq wofUpdateWorkflowReq) {

		Optional<WorkflowEntity> workflowEntityOptional = workflowRepository.findById(wofUpdateWorkflowReq.getId());
		if (!workflowEntityOptional.isPresent()) {
			throw new IsxAppException("作业流不存在");
		}

		WorkflowEntity workflow = workflowMapper.updateWorkflowReqToWorkflowEntity(wofUpdateWorkflowReq,
				workflowEntityOptional.get());

		workflowRepository.save(workflow);
	}

	public Page<PageWorkflowRes> pageWorkflow(PageWorkflowReq wocQueryWorkflowReq) {

		Page<WorkflowEntity> workflowEntityPage = workflowRepository.searchAll(wocQueryWorkflowReq.getSearchKeyWord(),
				PageRequest.of(wocQueryWorkflowReq.getPage(), wocQueryWorkflowReq.getPageSize()));

		Page<PageWorkflowRes> pageWorkflowRes = workflowEntityPage
				.map(workflowMapper::workflowEntityToQueryWorkflowRes);

		// 翻译集群名称
		pageWorkflowRes.getContent().forEach(e -> {
			if (!Strings.isEmpty(e.getDefaultClusterId())) {
				e.setClusterName(clusterRepository.findById(e.getDefaultClusterId()).get().getName());
			}
		});

		return pageWorkflowRes;
	}

	public void deleteWorkflow(DeleteWorkflowReq deleteWorkflowReq) {

		workflowRepository.deleteById(deleteWorkflowReq.getWorkflowId());
	}

	/**
	 * 运行工作流，返回工作流实例id
	 */
	public String runWorkflow(RunWorkflowReq runWorkflowReq) {

		// 获取工作流配置id
		WorkflowEntity workflow = getWorkflowEntity(runWorkflowReq.getWorkflowId());

		// 获取作业配置
		WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

		if (JSON.parseArray(workflowConfig.getNodeList(), String.class).isEmpty()) {
			throw new IsxAppException("节点为空，不能运行");
		}

		// 初始化作业流日志
		String runLog = LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始执行";

		// 创建工作流实例
		WorkflowInstanceEntity workflowInstance = WorkflowInstanceEntity.builder()
				.flowId(runWorkflowReq.getWorkflowId()).webConfig(workflowConfig.getWebConfig())
				.status(FlowInstanceStatus.RUNNING).instanceType(InstanceType.MANUAL).execStartDateTime(new Date())
				.runLog(runLog).build();
		workflowInstance = workflowInstanceRepository.saveAndFlush(workflowInstance);

		workflowInstanceRepository.setWorkflowLog(workflowInstance.getId(), runLog);

		// 初始化所有节点的作业实例
		List<String> nodeList = JSON.parseArray(workflowConfig.getNodeList(), String.class);
		List<WorkInstanceEntity> workInstances = new ArrayList<>();
		for (String workId : nodeList) {
			WorkInstanceEntity metaInstance = WorkInstanceEntity.builder().workId(workId)
					.instanceType(InstanceType.MANUAL).status(InstanceStatus.PENDING)
					.workflowInstanceId(workflowInstance.getId()).build();
			workInstances.add(metaInstance);
		}
		workInstanceRepository.saveAllAndFlush(workInstances);

		// 获取startNode
		List<String> startNodes = JSON.parseArray(workflowConfig.getDagStartList(), String.class);
		List<String> endNodes = JSON.parseArray(workflowConfig.getDagEndList(), String.class);
		List<List<String>> nodeMapping = JSON.parseObject(workflowConfig.getNodeMapping(),
				new TypeReference<List<List<String>>>() {
				});

		// 封装event推送时间，开始执行任务
		// 异步触发工作流
		List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
		for (WorkEntity work : startNodeWorks) {
			WorkflowRunEvent metaEvent = WorkflowRunEvent.builder().workId(work.getId()).workName(work.getName())
					.dagEndList(endNodes).dagStartList(startNodes).flowInstanceId(workflowInstance.getId())
					.nodeMapping(nodeMapping).nodeList(nodeList).tenantId(TENANT_ID.get()).userId(USER_ID.get())
					.build();
			eventPublisher.publishEvent(metaEvent);
		}

		return workflowInstance.getId();
	}

	public GetRunWorkInstancesRes getRunWorkInstances(GetRunWorkInstancesReq getRunWorkInstancesReq) {

		// 查询工作流实例
		Optional<WorkflowInstanceEntity> workflowInstanceEntityOptional = workflowInstanceRepository
				.findById(getRunWorkInstancesReq.getWorkflowInstanceId());
		if (!workflowInstanceEntityOptional.isPresent()) {
			throw new IsxAppException("工作流实例不存在");
		}
		WorkflowInstanceEntity workflowInstance = workflowInstanceEntityOptional.get();

		// 查询作业实例列表
		List<WorkInstanceEntity> workInstances = workInstanceRepository
				.findAllByWorkflowInstanceId(getRunWorkInstancesReq.getWorkflowInstanceId());
		List<WorkInstanceInfo> instanceInfos = workflowMapper
				.workInstanceEntityListToWorkInstanceInfoList(workInstances);

		// 封装返回对象
		return GetRunWorkInstancesRes.builder().flowStatus(workflowInstance.getStatus())
				.flowRunLog(workflowInstance.getRunLog()).workInstances(instanceInfos).build();
	}

	public GetWorkflowRes getWorkflow(GetWorkflowReq getWorkflowReq) {

		WorkflowEntity workflow = getWorkflowEntity(getWorkflowReq.getWorkflowId());

		WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

		GetWorkflowRes wofGetWorkflowRes = new GetWorkflowRes();
		wofGetWorkflowRes.setWebConfig(JSON.parse(workflowConfig.getWebConfig()));

		if (!Strings.isEmpty(workflowConfig.getCronConfig())) {
			wofGetWorkflowRes.setCronConfig(JSON.parseObject(workflowConfig.getCronConfig(), CronConfig.class));
		}

		return wofGetWorkflowRes;
	}

	/**
	 * 导出作业或者作业流
	 */
	public void exportWorkflow(ExportWorkflowReq wofExportWorkflowReq, HttpServletResponse response) {

		// 导出作业流
		WorkflowEntity workflow = new WorkflowEntity();
		WorkflowConfigEntity workflowConfig = new WorkflowConfigEntity();
		if (!Strings.isEmpty(wofExportWorkflowReq.getWorkflowId())) {
			workflow = workflowRepository.findById(wofExportWorkflowReq.getWorkflowId()).get();
			workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

			List<String> workIds = workRepository.findAllByWorkflowId(wofExportWorkflowReq.getWorkflowId()).stream()
					.map(WorkEntity::getId).collect(Collectors.toList());
			wofExportWorkflowReq.setWorkIds(workIds);
		}

		// 导出作业
		List<WorkExportInfo> works = new ArrayList<>();
		if (wofExportWorkflowReq.getWorkIds() != null && !wofExportWorkflowReq.getWorkIds().isEmpty()) {
			wofExportWorkflowReq.getWorkIds().forEach(workId -> {
				WorkEntity work = workRepository.findById(workId).get();
				WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
				WorkExportInfo metaExportInfo = WorkExportInfo.builder().workConfig(workConfig).work(work).build();
				works.add(metaExportInfo);
			});
		}

		WorkflowExportInfo workflowExportInfo = WorkflowExportInfo.builder().workflowConfig(workflowConfig)
				.workflow(workflow).works(works).build();

		// 导出的文件名
		String exportFileName;
		try {
			if (!Strings.isEmpty(wofExportWorkflowReq.getWorkflowId())) {
				exportFileName = URLEncoder.encode(workflow.getName() + "_all", String.valueOf(StandardCharsets.UTF_8));
			} else if (works.size() > 1) {
				exportFileName = URLEncoder.encode(workflow.getName() + "_" + works.size() + "_works",
						String.valueOf(StandardCharsets.UTF_8));
			} else if (works.size() == 1) {
				exportFileName = URLEncoder.encode(works.get(0).getWork().getName(),
						String.valueOf(StandardCharsets.UTF_8));
			} else {
				throw new IsxAppException("作业数量为空不能导出");
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			throw new IsxAppException(e.getMessage());
		}

		// 生成json并导出
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + exportFileName + ".json");

		// 导出
		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(JSON.toJSONString(workflowExportInfo));
			out.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new IsxAppException(e.getMessage());
		}
	}

	public void importWorkflow(MultipartFile workFile, String workflowId) {

		// 解析文本
		String exportWorkflowInfoStr;
		try {
			exportWorkflowInfoStr = new String(workFile.getBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new IsxAppException(e.getMessage());
		}

		// 获取对象
		WorkflowExportInfo workflowExportInfo = JSON.parseObject(exportWorkflowInfoStr, WorkflowExportInfo.class);

		// 导入作业流
		WorkflowConfigEntity workflowConfigEntity;
		if (Strings.isEmpty(workflowId)) {

			// 初始化作业流配置
			WorkflowConfigEntity workflowConfig = workflowExportInfo.getWorkflowConfig();
			workflowConfig.setId(null);
			workflowConfig.setVersionNumber(null);
			workflowConfig.setCreateBy(null);
			workflowConfig.setCreateDateTime(null);
			workflowConfig.setLastModifiedBy(null);
			workflowConfig.setLastModifiedDateTime(null);
			workflowConfig.setTenantId(null);
			workflowConfigEntity = workflowConfigRepository.save(workflowConfig);

			// 导入作业流
			WorkflowEntity workflow = workflowExportInfo.getWorkflow();
			workflow.setId(null);
			workflow.setVersionId(null);
			workflow.setConfigId(workflowConfigEntity.getId());
			workflow.setStatus(WorkflowStatus.UN_AUTO);
			workflow.setVersionNumber(null);
			workflow.setCreateBy(null);
			workflow.setCreateDateTime(null);
			workflow.setLastModifiedBy(null);
			workflow.setLastModifiedDateTime(null);
			workflow.setTenantId(null);
			WorkflowEntity workflowEntity = workflowRepository.save(workflow);
			workflowId = workflowEntity.getId();
		} else {
			workflowConfigEntity = null;
		}

		// 记录新的作业id
		Map<String, String> workIdMapping = new HashMap<>();

		if (Strings.isEmpty(workflowId)) {
			throw new IsxAppException("作业导入不能没有作业流id");
		}

		// 导入作业
		for (WorkExportInfo exportWork : workflowExportInfo.getWorks()) {

			// 旧的workId
			String oldWorkId = exportWork.getWork().getId();

			// 保存作业配置
			WorkConfigEntity workConfig = exportWork.getWorkConfig();

			workConfig.setId(null);
			workConfig.setVersionNumber(null);
			workConfig.setCreateBy(null);
			workConfig.setCreateDateTime(null);
			workConfig.setLastModifiedBy(null);
			workConfig.setLastModifiedDateTime(null);
			workConfig.setTenantId(null);
			workConfig = workConfigRepository.save(workConfig);

			// 保存作业
			WorkEntity work = exportWork.getWork();
			work.setId(null);
			work.setStatus(WorkStatus.UN_PUBLISHED);
			work.setWorkflowId(workflowId);
			work.setConfigId(workConfig.getId());
			work.setVersionId(null);
			work.setTopIndex(null);
			work.setVersionNumber(null);
			work.setCreateBy(null);
			work.setCreateDateTime(null);
			work.setLastModifiedBy(null);
			work.setLastModifiedDateTime(null);
			work.setTenantId(null);
			work = workRepository.save(work);

			// 记录workId映射关系
			workIdMapping.put(oldWorkId, work.getId());
		}

		// 最后再翻译一下，新的作业流配置
		if (workflowConfigEntity != null) {

			workIdMapping.forEach((oldWorkId, newWorkId) -> {
				workflowConfigEntity
						.setDagStartList(workflowConfigEntity.getDagStartList().replace(oldWorkId, newWorkId));
				workflowConfigEntity
						.setNodeMapping(workflowConfigEntity.getNodeMapping().replace(oldWorkId, newWorkId));
				workflowConfigEntity.setDagEndList(workflowConfigEntity.getDagEndList().replace(oldWorkId, newWorkId));
				workflowConfigEntity.setNodeList(workflowConfigEntity.getNodeList().replace(oldWorkId, newWorkId));
				workflowConfigEntity.setWebConfig(workflowConfigEntity.getWebConfig().replace(oldWorkId, newWorkId));
			});
			workflowConfigRepository.save(workflowConfigEntity);
		}
	}

	/**
	 * 中止作业流
	 */
	public void abortFlow(AbortFlowReq abortFlowReq) {

		// 将所有的PENDING作业实例，改为ABORT
		List<WorkInstanceEntity> pendingWorkInstances = workInstanceRepository
				.findAllByWorkflowInstanceIdAndStatus(abortFlowReq.getWorkflowInstanceId(), InstanceStatus.PENDING);
		pendingWorkInstances.forEach(workInstance -> {
			workInstance.setStatus(InstanceStatus.ABORT);
		});
		workInstanceRepository.saveAllAndFlush(pendingWorkInstances);

		// 将所有的RUNNING作业实例，改为ABORTING
		List<WorkInstanceEntity> runningWorkInstances = workInstanceRepository
				.findAllByWorkflowInstanceIdAndStatus(abortFlowReq.getWorkflowInstanceId(), InstanceStatus.RUNNING);
		runningWorkInstances.forEach(workInstance -> {
			workInstance.setStatus(InstanceStatus.ABORTING);
		});
		workInstanceRepository.saveAllAndFlush(runningWorkInstances);

		// 将工作流改为ABORTING
		WorkflowInstanceEntity lastWorkflowInstance = workflowInstanceRepository
				.findById(abortFlowReq.getWorkflowInstanceId()).get();
		lastWorkflowInstance.setStatus(InstanceStatus.ABORTING);
		workflowInstanceRepository.saveAndFlush(lastWorkflowInstance);

		// 异步调用中止作业的方法
		CompletableFuture.supplyAsync(() -> {
			runningWorkInstances.forEach(e -> {
				Integer locked = locker.lockOnly("ABORT_" + e.getWorkflowInstanceId());
				sparkYunWorkThreadPool.execute(() -> {
					try {
						abortWorkInstance(e);
					} finally {
						locker.unlock(locked);
					}
				});
			});
			return "SUCCESS";
		}).whenComplete((exeStatus, exception) -> {
			Integer lock = locker.lock("ABORT_" + abortFlowReq.getWorkflowInstanceId());
			try {
				WorkflowInstanceEntity workflowInstance = workflowInstanceRepository
						.findById(abortFlowReq.getWorkflowInstanceId()).get();
				workflowInstance.setStatus(InstanceStatus.ABORT);
				workflowInstance.setExecEndDateTime(new Date());
				workflowInstance.setDuration(
						(System.currentTimeMillis() - workflowInstance.getExecStartDateTime().getTime()) / 1000);
				workflowInstanceRepository.saveAndFlush(workflowInstance);
			} finally {
				locker.unlock(lock);
			}
		});
	}

	/**
	 * 中止作业节点实例.
	 */
	public void abortWorkInstance(WorkInstanceEntity workInstance) {

		WorkEntity workEntity = workRepository.findById(workInstance.getWorkId()).get();
		WorkExecutor workExecutor = workExecutorFactory.create(workEntity.getWorkType());
		workInstance = workInstanceRepository.findById(workInstance.getId()).get();

		try {
			workExecutor.syncAbort(workInstance);
			String submitLog = workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "已中止  \n";
			workInstance.setSubmitLog(submitLog);
			workInstance.setStatus(InstanceStatus.ABORT);
			workInstance.setExecEndDateTime(new Date());
			workInstance
					.setDuration((System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
			workInstanceRepository.save(workInstance);
		} catch (Exception e) {
			log.error(e.getMessage());
			String submitLog = workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "中止失败 \n";
			workInstance.setSubmitLog(submitLog);
			workInstance.setStatus(InstanceStatus.FAIL);
			workInstance.setExecEndDateTime(new Date());
			workInstance
					.setDuration((System.currentTimeMillis() - workInstance.getExecStartDateTime().getTime()) / 1000);
			workInstanceRepository.save(workInstance);
		}
	}

	/**
	 * 中断作业.
	 */
	public void breakFlow(BreakFlowReq breakFlowReq) {

		WorkInstanceEntity workInstance = workflowService.getWorkInstance(breakFlowReq.getWorkInstanceId());
		if (!InstanceStatus.PENDING.equals(workInstance.getStatus())) {
			throw new IsxAppException("只有等待中的作业可以中断");
		}
		workInstance.setStatus(InstanceStatus.BREAK);
		workInstanceRepository.save(workInstance);
	}

	/**
	 * 重跑当前节点.
	 */
	public void runCurrentNode(RunCurrentNodeReq runCurrentNodeReq) {

		WorkInstanceEntity workInstance = workInstanceRepository.findById(runCurrentNodeReq.getWorkInstanceId()).get();
		WorkflowInstanceEntity workflowInstance = workflowInstanceRepository
				.findById(workInstance.getWorkflowInstanceId()).get();
		WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
		WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();

		CompletableFuture.supplyAsync(() -> {
			workflowInstance.setStatus(InstanceStatus.RUNNING);
			workflowInstanceRepository.save(workflowInstance);

			// 同步执行作业
			WorkExecutor workExecutor = workExecutorFactory.create(work.getWorkType());
			WorkRunContext workRunContext = WorkflowUtils.genWorkRunContext(runCurrentNodeReq.getWorkInstanceId(), work,
					workConfig);
			workExecutor.syncExecute(workRunContext);

			return "OVER";
		}).whenComplete((exception, result) -> {
			List<WorkInstanceEntity> workInstances = workInstanceRepository
					.findAllByWorkflowInstanceId(workflowInstance.getId());
			boolean flowIsRunning = workInstances.stream().anyMatch(
					e -> InstanceStatus.RUNNING.equals(e.getStatus()) || InstanceStatus.PENDING.equals(e.getStatus()));
			if (!flowIsRunning) {

				boolean flowError = workInstances.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));

				if (flowError) {
					workflowInstance.setStatus(InstanceStatus.FAIL);
				} else {
					workflowInstance.setStatus(InstanceStatus.SUCCESS);
				}
				workflowInstance.setExecEndDateTime(new Date());
				workflowInstance.setDuration(
						(System.currentTimeMillis() - workflowInstance.getExecStartDateTime().getTime()) / 1000);
				workflowInstanceRepository.saveAndFlush(workflowInstance);
			}
		});
	}

	/**
	 * 重跑作业流.
	 */
	public void reRunFlow(ReRunFlowReq reRunFlowReq) {

		// 先中止作业
		// 将所有的PENDING作业实例，改为ABORT
		List<WorkInstanceEntity> pendingWorkInstances = workInstanceRepository
				.findAllByWorkflowInstanceIdAndStatus(reRunFlowReq.getWorkflowInstanceId(), InstanceStatus.PENDING);
		pendingWorkInstances.forEach(workInstance -> {
			workInstance.setStatus(InstanceStatus.ABORT);
			workInstance.setSparkStarRes(null);
			workInstance.setSubmitLog(null);
			workInstance.setYarnLog(null);
		});
		workInstanceRepository.saveAll(pendingWorkInstances);

		// 将所有的RUNNING作业实例，改为ABORTING
		List<WorkInstanceEntity> runningWorkInstances = workInstanceRepository
				.findAllByWorkflowInstanceIdAndStatus(reRunFlowReq.getWorkflowInstanceId(), InstanceStatus.RUNNING);
		runningWorkInstances.forEach(workInstance -> {
			workInstance.setStatus(InstanceStatus.ABORTING);
		});
		workInstanceRepository.saveAll(runningWorkInstances);

		// 初始化工作流实例状态
		WorkflowInstanceEntity workflowInstance = workflowInstanceRepository
				.findById(reRunFlowReq.getWorkflowInstanceId()).get();
		workflowInstance.setStatus(InstanceStatus.ABORTING);
		workflowInstanceRepository.saveAndFlush(workflowInstance);

		// 异步调用中止作业的方法
		CompletableFuture.supplyAsync(() -> {
			runningWorkInstances.forEach(e -> {
				Integer locked = locker.lockOnly("RERUN_" + e.getWorkflowInstanceId());
				sparkYunWorkThreadPool.execute(() -> {
					try {
						abortWorkInstance(e);
					} finally {
						locker.unlock(locked);
					}
				});
			});
			return "SUCCESS";
		}).whenComplete((exeStatus, exception) -> {

			// 中止完作业后，重新运行
			Integer lock = locker.lock("RERUN_" + reRunFlowReq.getWorkflowInstanceId());
			locker.unlock(lock);

			// 初始化作业实例状态
			List<WorkInstanceEntity> workInstances = workInstanceRepository
					.findAllByWorkflowInstanceId(reRunFlowReq.getWorkflowInstanceId());
			workInstances.forEach(e -> {
				e.setStatus(InstanceStatus.PENDING);
				e.setDuration(null);
				e.setExecStartDateTime(null);
				e.setExecEndDateTime(null);
				e.setQuartzHasRun(true);
			});
			workInstanceRepository.saveAllAndFlush(workInstances);

			// 初始化工作流实例状态
			WorkflowInstanceEntity workflowInstance2 = workflowInstanceRepository
					.findById(reRunFlowReq.getWorkflowInstanceId()).get();
			workflowInstance2.setStatus(InstanceStatus.RUNNING);
			workflowInstance2.setExecStartDateTime(new Date());
			workflowInstance2.setExecEndDateTime(null);
			workflowInstance2.setDuration(null);
			workflowInstanceRepository.saveAndFlush(workflowInstance2);

			// 获取配置工作流配置信息
			WorkflowEntity workflow = workflowRepository.findById(workflowInstance.getFlowId()).get();
			WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

			// 清理锁
			locker.clearLock(reRunFlowReq.getWorkflowInstanceId());

			// 重新执行
			List<String> startNodes = JSON.parseArray(workflowConfig.getDagStartList(), String.class);
			List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
			for (WorkEntity work : startNodeWorks) {
				WorkflowRunEvent metaEvent = WorkflowRunEvent.builder().workId(work.getId()).workName(work.getName())
						.dagEndList(JSON.parseArray(workflowConfig.getDagEndList(), String.class))
						.dagStartList(startNodes).flowInstanceId(workflowInstance.getId()).nodeMapping(JSON
								.parseObject(workflowConfig.getNodeMapping(), new TypeReference<List<List<String>>>() {
								}))
						.nodeList(JSON.parseArray(workflowConfig.getNodeList(), String.class)).tenantId(TENANT_ID.get())
						.userId(USER_ID.get()).build();
				eventPublisher.publishEvent(metaEvent);
			}
		});
	}

	public void runAfterFlow(RunAfterFlowReq runAfterFlowReq) {

		WorkInstanceEntity workInstance = workInstanceRepository.findById(runAfterFlowReq.getWorkInstanceId()).get();
		WorkflowInstanceEntity workflowInstance = workflowInstanceRepository
				.findById(workInstance.getWorkflowInstanceId()).get();

		// 作业流状态改为RUNNING
		workflowInstance.setStatus(InstanceStatus.RUNNING);
		workflowInstanceRepository.save(workflowInstance);

		WorkflowEntity workflow = workflowRepository.findById(workflowInstance.getFlowId()).get();
		WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

		// 将下游所有节点，全部状态改为PENDING
		List<List<String>> nodeMapping = JSON.parseObject(workflowConfig.getNodeMapping(),
				new TypeReference<List<List<String>>>() {
				});

		WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
		List<String> afterWorkIds = WorkflowUtils.parseAfterNodes(nodeMapping, work.getId());

		// 将下游所有节点实例，全部状态改为PENDING
		List<WorkInstanceEntity> afterWorkInstances = workInstanceRepository
				.findAllByWorkflowInstanceIdAndWorkIds(workInstance.getWorkflowInstanceId(), afterWorkIds);
		afterWorkInstances.forEach(e -> {
			e.setStatus(InstanceStatus.PENDING);
			e.setSparkStarRes(null);
			e.setSubmitLog(null);
			e.setYarnLog(null);
		});
		workInstanceRepository.saveAllAndFlush(afterWorkInstances);

		// 清空锁
		locker.clearLock(workflowInstance.getId());

		// 推送一次
		WorkflowRunEvent metaEvent = WorkflowRunEvent.builder().workId(work.getId()).workName(work.getName())
				.dagEndList(JSON.parseArray(workflowConfig.getDagEndList(), String.class))
				.dagStartList(JSON.parseArray(workflowConfig.getDagStartList(), String.class))
				.flowInstanceId(workflowInstance.getId()).nodeMapping(nodeMapping)
				.nodeList(JSON.parseArray(workflowConfig.getNodeList(), String.class)).tenantId(TENANT_ID.get())
				.userId(USER_ID.get()).build();
		eventPublisher.publishEvent(metaEvent);
	}

	public GetWorkflowDefaultClusterRes getWorkflowDefaultCluster(
			GetWorkflowDefaultClusterReq getWorkflowDefaultClusterReq) {

		// 查询工作流的默认计算引擎
		WorkflowEntity workflow = workflowService.getWorkflow(getWorkflowDefaultClusterReq.getWorkflowId());

		if (Strings.isEmpty(workflow.getDefaultClusterId())) {
			return null;
		}

		ClusterEntity cluster = clusterService.getCluster(workflow.getDefaultClusterId());
		return GetWorkflowDefaultClusterRes.builder().clusterId(cluster.getId()).clusterName(cluster.getName()).build();
	}

}
