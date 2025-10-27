package com.isxcode.spark.modules.workflow.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.spark.api.instance.constants.FlowInstanceStatus;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.instance.constants.InstanceType;
import com.isxcode.spark.api.work.constants.EventType;
import com.isxcode.spark.api.workflow.dto.WorkflowToken;
import com.isxcode.spark.api.workflow.req.GetInvokeUrlReq;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.utils.jwt.JwtUtils;
import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
import com.isxcode.spark.modules.work.entity.WorkEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.workflow.entity.WorkflowConfigEntity;
import com.isxcode.spark.modules.workflow.entity.WorkflowEntity;
import com.isxcode.spark.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.spark.modules.workflow.entity.WorkflowVersionEntity;
import com.isxcode.spark.modules.workflow.repository.WorkflowConfigRepository;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.spark.modules.workflow.repository.WorkflowRepository;
import com.isxcode.spark.modules.workflow.repository.WorkflowVersionRepository;
import com.isxcode.spark.modules.workflow.run.WorkflowRunEvent;
import com.isxcode.spark.modules.workflow.run.WorkflowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.isxcode.spark.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.spark.common.config.CommonConfig.USER_ID;

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

    private final WorkConfigRepository workConfigRepository;

    private final WorkRunJobFactory workRunJobFactory;

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

        // 获取工作流
        WorkflowEntity workflow = getWorkflow(workflowId);

        // 获取作业流配置
        WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

        if (workflowConfig.getNodeList() == null
            || JSON.parseArray(workflowConfig.getNodeList(), String.class).isEmpty()) {
            throw new IsxAppException("节点为空，请保存后运行");
        }

        // 初始化作业流状态
        WorkflowInstanceEntity workflowInstance = WorkflowInstanceEntity.builder().flowId(workflowId)
            .webConfig(workflowConfig.getWebConfig()).status(FlowInstanceStatus.RUNNING)
            .instanceType(InstanceType.MANUAL).execStartDateTime(new Date()).build();
        workflowInstance = workflowInstanceRepository.saveAndFlush(workflowInstance);

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

        // 作业流日志:手动启动作业流
        log.debug("【手动触发作业流】: {},【作业流实例id】: {}", workflow.getName(), workflowInstance.getId());

        // 封装定时器调度
        List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
        for (WorkEntity work : startNodeWorks) {

            // 拿作业的配置
            WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();

            // 查询作业的实例
            WorkInstanceEntity workInstance =
                workInstanceRepository.findByWorkIdAndWorkflowInstanceId(work.getId(), workflowInstance.getId());

            // 在执行器前，封装WorkRunContext
            WorkRunContext workRunContext =
                WorkflowUtils.genWorkRunContext(workInstance.getId(), EventType.WORKFLOW, work, workConfig);
            workRunContext.setDagEndList(endNodes);
            workRunContext.setDagStartList(endNodes);
            workRunContext.setFlowInstanceId(workflowInstance.getId());
            workRunContext.setNodeMapping(nodeMapping);
            workRunContext.setNodeList(nodeList);

            // 提交定时器
            workRunJobFactory.run(workRunContext);
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
