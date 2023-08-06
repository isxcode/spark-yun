package com.isxcode.star.modules.workflow.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.workflow.pojos.req.WfcConfigWorkflowReq;
import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.workflow.entity.WorkflowConfigEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowEntity;
import com.isxcode.star.modules.workflow.repository.WorkflowConfigRepository;
import com.isxcode.star.modules.workflow.run.WorkflowUtils;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkflowConfigBizService {

  private final WorkflowBizService workflowBizService;

  private final WorkflowConfigRepository workflowConfigRepository;

  private final WorkRepository workRepository;

  public WorkflowConfigEntity getWorkflowConfig(String workflowConfigId) {

    Optional<WorkflowConfigEntity> workflowConfigEntityOptional =
        workflowConfigRepository.findById(workflowConfigId);
    if (!workflowConfigEntityOptional.isPresent()) {
      throw new SparkYunException("工作流配置异常，不存在");
    }
    return workflowConfigEntityOptional.get();
  }

  /** 配置工作流. */
  public void configWorkflow(WfcConfigWorkflowReq wfcConfigWorkflowReq) {

    String flowWebConfig = JSON.toJSONString(wfcConfigWorkflowReq.getWebConfig());

    // 获取工作流
    WorkflowEntity workflow =
        workflowBizService.getWorkflowEntity(wfcConfigWorkflowReq.getWorkflowId());

    // 从webConfig中解析出所有节点
    List<String> nodeList = WorkflowUtils.parseNodeList(flowWebConfig);

    // 保存时检测作业是否存在，有bug，后面改
    List<WorkEntity> works = workRepository.findAllByWorkIds(nodeList);
    if (works.size() != nodeList.size()) {
      throw new SparkYunException("作业数量不一致，可能存在作业重复");
    }

    // 从webConfig中解析出所有节点映射
    List<List<String>> nodeMapping = WorkflowUtils.parseNodeMapping(flowWebConfig);

    // 检查节点是否闭环
    WorkflowUtils.checkFlow(nodeList, nodeMapping);

    // 解析开始节点
    List<String> startNodes = WorkflowUtils.getStartNodes(nodeMapping, nodeList);

    // 解析结束节点
    List<String> endNodes = WorkflowUtils.getEndNodes(nodeMapping, nodeList);

    // 获取工作流配置
    WorkflowConfigEntity workflowConfig = getWorkflowConfig(workflow.getConfigId());

    // 封装工作流配置
    workflowConfig.setWebConfig(flowWebConfig);
    workflowConfig.setDagEndList(JSON.toJSONString(endNodes));
    workflowConfig.setDagStartList(JSON.toJSONString(startNodes));
    workflowConfig.setNodeList(JSON.toJSONString(nodeList));
    workflowConfig.setNodeMapping(JSON.toJSONString(nodeMapping));

    // 持久化配置
    workflowConfigRepository.save(workflowConfig);
  }
}
