package com.isxcode.star.modules.workflow.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.instance.constants.FlowInstanceStatus;
import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.instance.constants.InstanceType;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.workflow.dto.WorkflowToken;
import com.isxcode.star.api.workflow.req.GetInvokeUrlReq;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.jwt.JwtUtils;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.workflow.entity.WorkflowConfigEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowVersionEntity;
import com.isxcode.star.modules.workflow.repository.WorkflowConfigRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowRepository;
import com.isxcode.star.modules.workflow.repository.WorkflowVersionRepository;
import com.isxcode.star.modules.workflow.run.WorkflowRunEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.common.config.CommonConfig.USER_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkInstanceRepository workInstanceRepository;

    private final WorkflowInstanceRepository workflowInstanceRepository;

    private final WorkflowRepository workflowRepository;

    private final WorkRepository workRepository;

    private final WorkflowConfigRepository workflowConfigRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final IsxAppProperties isxAppProperties;

    private final ServerProperties serverProperties;

    private final WorkflowVersionRepository workflowVersionRepository;

    public WorkInstanceEntity getWorkInstance(String workInstanceId) {

        return workInstanceRepository.findById(workInstanceId).orElseThrow(() -> new IsxAppException("实例不存在"));
    }

    public WorkflowInstanceEntity getWorkflowInstance(String workflowInstanceId) {

        return workflowInstanceRepository.findById(workflowInstanceId).orElseThrow(() -> new IsxAppException("实例不存在"));
    }

    public WorkflowEntity getWorkflow(String workflowId) {

        return workflowRepository.findById(workflowId).orElseThrow(() -> new IsxAppException("工作流不存在"));
    }

    public WorkflowConfigEntity getWorkflowConfig(String workflowConfigId) {

        return workflowConfigRepository.findById(workflowConfigId).orElseThrow(() -> new IsxAppException("工作流配置不存在"));
    }

    /**
     * 生成外部调用的链接.
     */
    public String getInvokeUrl(GetInvokeUrlReq getInvokeUrlReq) {

        String httpProtocol = isxAppProperties.isUseSsl() ? "https://" : "http://";
        String httpUrlBuilder = httpProtocol + getInvokeUrlReq.getOuterAddress() + "/workflow/open/invokeWorkflow";

        WorkflowToken workflowToken = WorkflowToken.builder().userId(USER_ID.get()).tenantId(TENANT_ID.get())
            .workflowId(getInvokeUrlReq.getWorkflowId()).type("WORKFLOW_INVOKE").build();
        String token =
            JwtUtils.encrypt(isxAppProperties.getAesSlat(), workflowToken, isxAppProperties.getJwtKey(), 365 * 24 * 60);

        return "curl -s '" + httpUrlBuilder + "' \\\n" + "   -H 'Content-Type: application/json;charset=UTF-8' \\\n"
            + "   -H 'Accept: application/json, text/plain, */*' \\\n" + "   --data-raw '{\"workflowId\":\""
            + getInvokeUrlReq.getWorkflowId() + "\",\"token\":\"" + token + "\"}'";
    }

    /**
     * 运行工作流，返回工作流实例id
     */
    public String runWorkflow(String workflowId) {

        // 获取工作流配置id
        WorkflowEntity workflow = getWorkflow(workflowId);

        // 获取作业配置
        WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

        if (workflowConfig.getNodeList() == null
            || JSON.parseArray(workflowConfig.getNodeList(), String.class).isEmpty()) {
            throw new IsxAppException("节点为空，请保存后运行");
        }

        // 初始化作业流日志
        String runLog = LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始执行";

        // 创建工作流实例
        WorkflowInstanceEntity workflowInstance = WorkflowInstanceEntity.builder().flowId(workflowId)
            .webConfig(workflowConfig.getWebConfig()).status(FlowInstanceStatus.RUNNING)
            .instanceType(InstanceType.MANUAL).execStartDateTime(new Date()).runLog(runLog).build();
        workflowInstance = workflowInstanceRepository.saveAndFlush(workflowInstance);

        workflowInstanceRepository.setWorkflowLog(workflowInstance.getId(), runLog);

        // 初始化所有节点的作业实例
        List<String> nodeList = JSON.parseArray(workflowConfig.getNodeList(), String.class);
        List<WorkInstanceEntity> workInstances = new ArrayList<>();
        for (String workId : nodeList) {
            WorkInstanceEntity metaInstance =
                WorkInstanceEntity.builder().workId(workId).instanceType(InstanceType.MANUAL)
                    .status(InstanceStatus.PENDING).workflowInstanceId(workflowInstance.getId()).build();
            workInstances.add(metaInstance);
        }
        workInstanceRepository.saveAllAndFlush(workInstances);

        // 获取startNode
        List<String> startNodes = JSON.parseArray(workflowConfig.getDagStartList(), String.class);
        List<String> endNodes = JSON.parseArray(workflowConfig.getDagEndList(), String.class);
        List<List<String>> nodeMapping =
            JSON.parseObject(workflowConfig.getNodeMapping(), new TypeReference<List<List<String>>>() {});

        // 封装event推送时间，开始执行任务
        // 异步触发工作流
        List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
        for (WorkEntity work : startNodeWorks) {
            WorkflowRunEvent metaEvent = WorkflowRunEvent.builder().workId(work.getId()).workName(work.getName())
                .dagEndList(endNodes).dagStartList(startNodes).flowInstanceId(workflowInstance.getId())
                .nodeMapping(nodeMapping).nodeList(nodeList).tenantId(TENANT_ID.get()).userId(USER_ID.get()).build();
            eventPublisher.publishEvent(metaEvent);
        }

        return workflowInstance.getId();
    }

    public String runInvokeWorkflow(String workflowId) {

        // 获取工作流配置id
        WorkflowEntity workflow = getWorkflow(workflowId);

        // 获取工作流的版本信息
        WorkflowVersionEntity workflowVersion = workflowVersionRepository.findById(workflow.getVersionId()).get();

        // 初始化工作流实例
        WorkflowInstanceEntity workflowInstance =
            WorkflowInstanceEntity.builder().flowId(workflowVersion.getWorkflowId()).status(FlowInstanceStatus.RUNNING)
                .instanceType(InstanceType.INVOKE).versionId(workflow.getVersionId())
                .webConfig(workflowVersion.getWebConfig()).execStartDateTime(new Date()).build();
        workflowInstance = workflowInstanceRepository.saveAndFlush(workflowInstance);

        // 初始化作业实例
        List<String> nodeList = com.alibaba.fastjson.JSON.parseArray(workflowVersion.getNodeList(), String.class);
        List<WorkInstanceEntity> workInstances = new ArrayList<>();
        List<WorkEntity> allWorks = workRepository.findAllById(nodeList);
        for (WorkEntity metaWork : allWorks) {
            WorkInstanceEntity metaInstance = WorkInstanceEntity.builder().workId(metaWork.getId())
                .versionId(metaWork.getVersionId()).instanceType(InstanceType.INVOKE).status(InstanceStatus.PENDING)
                .quartzHasRun(true).workflowInstanceId(workflowInstance.getId()).build();
            workInstances.add(metaInstance);
        }

        workInstanceRepository.saveAllAndFlush(workInstances);

        // 直接执行一次作业流的点击
        List<String> startNodes = com.alibaba.fastjson.JSON.parseArray(workflowVersion.getDagStartList(), String.class);
        List<String> endNodes = com.alibaba.fastjson.JSON.parseArray(workflowVersion.getDagEndList(), String.class);
        List<List<String>> nodeMapping = com.alibaba.fastjson.JSON.parseObject(workflowVersion.getNodeMapping(),
            new TypeReference<List<List<String>>>() {});

        // 封装event推送时间，开始执行任务
        // 异步触发工作流
        List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
        for (WorkEntity work : startNodeWorks) {
            WorkflowRunEvent metaEvent = WorkflowRunEvent.builder().workId(work.getId()).workName(work.getName())
                .dagEndList(endNodes).dagStartList(startNodes).flowInstanceId(workflowInstance.getId())
                .versionId(work.getVersionId()).nodeMapping(nodeMapping).nodeList(nodeList).tenantId(TENANT_ID.get())
                .userId(USER_ID.get()).build();
            eventPublisher.publishEvent(metaEvent);
        }

        return workflowInstance.getId();
    }
}
