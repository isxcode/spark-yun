package com.isxcode.star.backend.module.workflow.config;

import com.alibaba.fastjson.JSON;
import com.isxcode.star.api.exceptions.SparkYunException;
import com.isxcode.star.api.pojos.workflow.config.req.WfcConfigWorkflowReq;
import com.isxcode.star.backend.module.work.WorkEntity;
import com.isxcode.star.backend.module.work.WorkRepository;
import com.isxcode.star.backend.module.workflow.WorkflowBizService;
import com.isxcode.star.backend.module.workflow.WorkflowEntity;
import com.isxcode.star.backend.module.workflow.run.WorkflowUtils;
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

    // 获取工作流
    WorkflowEntity workflow =
        workflowBizService.getWorkflowEntity(wfcConfigWorkflowReq.getWorkflowId());

    // 从webConfig中解析出所有节点
    List<String> nodeList = WorkflowUtils.parseNodeList(wfcConfigWorkflowReq.getWebConfig());

    // 保存时检测作业是否存在，有bug，后面改
    List<WorkEntity> works = workRepository.findAllByWorkIds(nodeList);
    if (works.size() != nodeList.size()) {
      throw new SparkYunException("作业数量不一致，可能存在作业重复");
    }

    // 从webConfig中解析出所有节点映射
    List<List<String>> nodeMapping =
        WorkflowUtils.parseNodeMapping(wfcConfigWorkflowReq.getWebConfig());

    // 检查节点是否闭环
    WorkflowUtils.checkFlow(nodeList, nodeMapping);

    // 解析开始节点
    List<String> startNodes = WorkflowUtils.getStartNodes(nodeMapping, nodeList);

    // 解析结束节点
    List<String> endNodes = WorkflowUtils.getEndNodes(nodeMapping, nodeList);

    // 获取工作流配置
    WorkflowConfigEntity workflowConfig = getWorkflowConfig(workflow.getConfigId());

    // 封装工作流配置
    workflowConfig.setWebConfig(wfcConfigWorkflowReq.getWebConfig());
    workflowConfig.setDagEndList(JSON.toJSONString(endNodes));
    workflowConfig.setDagStartList(JSON.toJSONString(startNodes));
    workflowConfig.setNodeList(JSON.toJSONString(nodeList));
    workflowConfig.setNodeMapping(JSON.toJSONString(nodeMapping));

    // 持久化配置
    workflowConfigRepository.save(workflowConfig);
  }
}
