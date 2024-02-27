package com.isxcode.star.modules.monitor.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.api.constants.ApiStatus;
import com.isxcode.star.api.cluster.constants.ClusterStatus;
import com.isxcode.star.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.star.api.datasource.constants.DatasourceStatus;
import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.main.properties.SparkYunProperties;
import com.isxcode.star.api.monitor.constants.MonitorStatus;
import com.isxcode.star.api.monitor.pojos.ao.WorkflowMonitorAo;
import com.isxcode.star.api.monitor.pojos.dto.MonitorLineDto;
import com.isxcode.star.api.monitor.pojos.dto.NodeMonitorInfo;
import com.isxcode.star.api.monitor.pojos.dto.SystemMonitorDto;
import com.isxcode.star.api.monitor.pojos.dto.WorkflowInstanceLineDto;
import com.isxcode.star.api.monitor.pojos.req.GetClusterMonitorReq;
import com.isxcode.star.api.monitor.pojos.req.GetInstanceMonitorReq;
import com.isxcode.star.api.monitor.pojos.req.PageInstancesReq;
import com.isxcode.star.api.monitor.pojos.res.GetClusterMonitorRes;
import com.isxcode.star.api.monitor.pojos.res.GetInstanceMonitorRes;
import com.isxcode.star.api.monitor.pojos.res.GetSystemMonitorRes;
import com.isxcode.star.api.monitor.pojos.res.PageInstancesRes;
import com.isxcode.star.api.workflow.constants.WorkflowStatus;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.common.utils.AesUtils;
import com.isxcode.star.modules.api.repository.ApiRepository;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.monitor.entity.MonitorEntity;
import com.isxcode.star.modules.monitor.mapper.MonitorMapper;
import com.isxcode.star.modules.monitor.repository.MonitorRepository;
import com.isxcode.star.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.star.modules.workflow.mapper.WorkflowMapper;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.isxcode.star.api.monitor.constants.TimeType;

import javax.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.isxcode.star.common.config.CommonConfig.JPA_TENANT_MODE;
import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.utils.ssh.SshUtils.executeCommand;
import static com.isxcode.star.common.utils.ssh.SshUtils.scpFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MonitorBizService {

	private final ClusterNodeRepository clusterNodeRepository;

	private final SparkYunProperties sparkYunProperties;

	private final ClusterNodeMapper clusterNodeMapper;

	private final MonitorMapper monitorMapper;

	private final AesUtils aesUtils;

	private final MonitorRepository monitorRepository;

	private final ClusterRepository clusterRepository;

	private final DatasourceRepository datasourceRepository;

	private final WorkflowRepository workflowRepository;

	private final WorkflowInstanceRepository workflowInstanceRepository;

	private final ApiRepository apiRepository;

	private final WorkflowMapper workflowMapper;

	public GetSystemMonitorRes getSystemMonitor() {

		// 集群信息
		long activeClusterNum = clusterRepository.countByStatus(ClusterStatus.ACTIVE);
		long allClusterNum = clusterRepository.count();
		SystemMonitorDto clusterMonitor = SystemMonitorDto.builder().total(allClusterNum).activeNum(activeClusterNum)
				.build();

		// 数据源信息
		long activeDatasourceNum = datasourceRepository.countByStatus(DatasourceStatus.ACTIVE);
		long allDatasourceNum = datasourceRepository.count();
		SystemMonitorDto datasourceMonitor = SystemMonitorDto.builder().total(allDatasourceNum)
				.activeNum(activeDatasourceNum).build();

		// 发布作业信息
		long publishedWorkflowNum = workflowRepository.countByStatus(WorkflowStatus.PUBLISHED);
		long allWorkflowNum = workflowRepository.count();
		SystemMonitorDto workMonitor = SystemMonitorDto.builder().total(allWorkflowNum).activeNum(publishedWorkflowNum)
				.build();

		// 发布接口信息
		long allApiNum = apiRepository.count();
		long publishedApiNum = apiRepository.countByStatus(ApiStatus.PUBLISHED);
		SystemMonitorDto apiMonitor = SystemMonitorDto.builder().total(allApiNum).activeNum(publishedApiNum).build();

		// 封装返回
		return GetSystemMonitorRes.builder().apiMonitor(apiMonitor).workflowMonitor(workMonitor)
				.clusterMonitor(clusterMonitor).datasourceMonitor(datasourceMonitor).build();
	}

	public GetClusterMonitorRes getClusterMonitor(GetClusterMonitorReq getClusterMonitorReq) {

		// 计算时间区间
		LocalDateTime endDateTime = LocalDateTime.now();
		LocalDateTime startDateTime;
		switch (getClusterMonitorReq.getTimeType()) {
			case TimeType.THIRTY_MIN :
				startDateTime = endDateTime.minusMinutes(30);
				break;
			case TimeType.ONE_HOUR :
				startDateTime = endDateTime.minusHours(1);
				break;
			case TimeType.TWO_HOUR :
				startDateTime = endDateTime.minusHours(2);
				break;
			case TimeType.SIX_HOUR :
				startDateTime = endDateTime.minusHours(6);
				break;
			case TimeType.TWELVE_HOUR :
				startDateTime = endDateTime.minusHours(12);
				break;
			case TimeType.ONE_DAY :
				startDateTime = endDateTime.minusDays(1);
				break;
			case TimeType.SEVEN_DAY :
				startDateTime = endDateTime.minusDays(7);
				break;
			case TimeType.THIRTY_DAY :
				startDateTime = endDateTime.minusDays(30);
				break;
			default :
				throw new IsxAppException("时间类型不支持");
		}

		// 查询记录数
		List<MonitorLineDto> monitorLine = monitorRepository.queryMonitorLine(getClusterMonitorReq.getClusterId(),
				startDateTime, endDateTime);

		// 返回结果
		return GetClusterMonitorRes.builder().line(monitorLine).build();
	}

	public GetInstanceMonitorRes getInstanceMonitor(GetInstanceMonitorReq getInstanceMonitorReq) {

		// 查询当天的实例
		DateTime startDateTime = DateUtil.beginOfDay(getInstanceMonitorReq.getLocalDate());
		DateTime endDateTime = DateUtil.endOfDay(getInstanceMonitorReq.getLocalDate());
		List<WorkflowInstanceEntity> workflowInstances = workflowInstanceRepository
				.findAllByExecStartDateTimeAfterAndExecEndDateTimeBefore(startDateTime, endDateTime);

		// 初始化数组
		List<WorkflowInstanceLineDto> lines = new ArrayList<>();
		long allNum = DateUtil.between(startDateTime, new Date(), DateUnit.HOUR);
		for (int i = 0; i < allNum; i++) {
			lines.add(WorkflowInstanceLineDto.builder().successNum(0L).failNum(0L).runningNum(0L).build());
		}

		// 逐条解析
		workflowInstances.forEach(e -> {

			// 开始小时和结束小时
			int startHour = DateUtil.hour(e.getExecStartDateTime(), true) - 1;
			int endHour = e.getExecStartDateTime() == null
					? Integer.parseInt(String.valueOf(allNum)) - 1
					: DateUtil.hour(e.getExecEndDateTime(), true) - 1;

			// 补充运行中的个数
			for (int i = startHour; i < endHour; i++) {
				lines.get(i).setRunningNum(lines.get(i).getRunningNum() + 1);
			}

			// 成功和失败的实例叠加
			if (InstanceStatus.FAIL.equals(e.getStatus())) {
				for (int i = startHour; i < Integer.parseInt(String.valueOf(allNum)); i++) {
					lines.get(i).setFailNum(lines.get(i).getFailNum() + 1);
				}
			} else if (InstanceStatus.SUCCESS.equals(e.getStatus())) {
				for (int i = startHour; i < Integer.parseInt(String.valueOf(allNum)); i++) {
					lines.get(i).setSuccessNum(lines.get(i).getSuccessNum() + 1);
				}
			}
		});

		return GetInstanceMonitorRes.builder().instanceNumLine(lines).build();
	}

	public Page<PageInstancesRes> pageInstances(PageInstancesReq pageInstancesReq) {

		JPA_TENANT_MODE.set(false);
		Page<WorkflowMonitorAo> workflowMonitorAos = workflowInstanceRepository.searchWorkflowMonitor(TENANT_ID.get(),
				pageInstancesReq.getSearchKeyWord(),
				PageRequest.of(pageInstancesReq.getPage(), pageInstancesReq.getPageSize()));

		return workflowMonitorAos.map(workflowMapper::workflowMonitorAoToPageInstancesRes);
	}

	@Scheduled(cron = "0 * * * * ?")
	public void scheduleGetNodeMonitor() {

		LocalDateTime now = LocalDateTime.now();

		// 获取所有的节点
		JPA_TENANT_MODE.set(false);
		List<ClusterNodeEntity> allNode = clusterNodeRepository.findAll();

		allNode.forEach(e -> {
			CompletableFuture.supplyAsync(() -> {

				// 封装ScpFileEngineNodeDto对象
				ScpFileEngineNodeDto scpFileEngineNodeDto = clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(e);
				scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

				// 每个节点都抽取一次
				try {
					NodeMonitorInfo nodeMonitor = getNodeMonitor(scpFileEngineNodeDto);
					nodeMonitor.setClusterNodeId(e.getId());
					nodeMonitor.setClusterId(e.getClusterId());
					nodeMonitor.setTenantId(e.getTenantId());
					nodeMonitor.setCreateDateTime(now);
					return nodeMonitor;
				} catch (Exception ex) {
					return NodeMonitorInfo.builder().clusterNodeId(e.getId()).clusterNodeId(e.getClusterId())
							.status(MonitorStatus.FAIL).log(ex.getMessage()).tenantId(e.getTenantId())
							.createDateTime(now).build();
				}
			}).whenComplete((result, throwable) -> {
				// 持久化到数据库
				MonitorEntity monitorEntity = monitorMapper.nodeMonitorInfoToMonitorEntity(result);
				monitorRepository.save(monitorEntity);
			});
		});
	}

	public NodeMonitorInfo getNodeMonitor(ScpFileEngineNodeDto scpFileEngineNodeDto)
			throws JSchException, IOException, InterruptedException, SftpException {

		// 拷贝检测脚本
		scpFile(scpFileEngineNodeDto, "classpath:bash/node-monitor.sh",
				sparkYunProperties.getTmpDir() + File.separator + "node-monitor.sh");

		// 运行安装脚本
		String getMonitorCommand = "bash " + sparkYunProperties.getTmpDir() + File.separator + "node-monitor.sh";

		// 获取返回结果
		String executeLog = executeCommand(scpFileEngineNodeDto, getMonitorCommand, false);

		// 获取节点信息
		return JSON.parseObject(executeLog, NodeMonitorInfo.class);
	}
}
