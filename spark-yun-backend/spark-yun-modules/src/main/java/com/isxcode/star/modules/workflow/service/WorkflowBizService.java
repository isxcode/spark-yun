package com.isxcode.star.modules.workflow.service;

import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.star.api.instance.constants.FlowInstanceStatus;
import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.instance.constants.InstanceType;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkStatus;
import com.isxcode.star.api.workflow.constants.WorkflowStatus;
import com.isxcode.star.api.workflow.pojos.dto.WorkInstanceInfo;
import com.isxcode.star.api.workflow.pojos.req.WocQueryWorkflowReq;
import com.isxcode.star.api.workflow.pojos.req.WofAddWorkflowReq;
import com.isxcode.star.api.workflow.pojos.req.WofExportWorkflowReq;
import com.isxcode.star.api.workflow.pojos.req.WofUpdateWorkflowReq;
import com.isxcode.star.api.workflow.pojos.res.WofGetWorkflowRes;
import com.isxcode.star.api.workflow.pojos.res.WofQueryRunWorkInstancesRes;
import com.isxcode.star.api.workflow.pojos.res.WofQueryWorkflowRes;
import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
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

/** 用户模块接口的业务逻辑. */
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

  public WorkflowEntity getWorkflowEntity(String workflowId) {

    Optional<WorkflowEntity> workflowEntityOptional = workflowRepository.findById(workflowId);
    if (!workflowEntityOptional.isPresent()) {
      throw new SparkYunException("作业流不存在");
    }
    return workflowEntityOptional.get();
  }

  public void addWorkflow(WofAddWorkflowReq wofAddWorkflowReq) {

    // 初始化工作流配置
    WorkflowConfigEntity workflowConfig = new WorkflowConfigEntity();
    workflowConfig = workflowConfigRepository.save(workflowConfig);

    // 工作流绑定配置
    WorkflowEntity workflow = workflowMapper.addWorkflowReqToWorkflowEntity(wofAddWorkflowReq);
    workflow.setStatus(WorkflowStatus.UN_AUTO);
    workflow.setConfigId(workflowConfig.getId());
    workflowRepository.save(workflow);
  }

  public void updateWorkflow(WofUpdateWorkflowReq wofUpdateWorkflowReq) {

    Optional<WorkflowEntity> workflowEntityOptional =
        workflowRepository.findById(wofUpdateWorkflowReq.getId());
    if (!workflowEntityOptional.isPresent()) {
      throw new SparkYunException("作业流不存在");
    }

    WorkflowEntity workflow =
        workflowMapper.updateWorkflowReqToWorkflowEntity(
            wofUpdateWorkflowReq, workflowEntityOptional.get());

    workflowRepository.save(workflow);
  }

  public Page<WofQueryWorkflowRes> queryWorkflow(WocQueryWorkflowReq wocQueryWorkflowReq) {

    Page<WorkflowEntity> workflowEntityPage =
        workflowRepository.searchAll(
            wocQueryWorkflowReq.getSearchKeyWord(),
            PageRequest.of(wocQueryWorkflowReq.getPage(), wocQueryWorkflowReq.getPageSize()));

    return workflowMapper.workflowEntityPageToQueryWorkflowResPage(workflowEntityPage);
  }

  public void delWorkflow(String workflowId) {

    workflowRepository.deleteById(workflowId);
  }

  /** 运行工作流，返回工作流实例id */
  public String runFlow(String workflowId) {

    // 获取工作流配置id
    WorkflowEntity workflow = getWorkflowEntity(workflowId);

    // 获取作业配置
    WorkflowConfigEntity workflowConfig =
        workflowConfigRepository.findById(workflow.getConfigId()).get();

    // 初始化作业流日志
    String runLog = LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始执行";

    // 创建工作流实例
    WorkflowInstanceEntity workflowInstance =
        WorkflowInstanceEntity.builder()
            .flowId(workflowId)
            .webConfig(workflowConfig.getWebConfig())
            .status(FlowInstanceStatus.RUNNING)
            .instanceType(InstanceType.MANUAL)
            .execStartDateTime(new Date())
            .runLog(runLog)
            .build();
    workflowInstance = workflowInstanceRepository.saveAndFlush(workflowInstance);

    workflowInstanceRepository.setWorkflowLog(workflowInstance.getId(), runLog);

    // 初始化所有节点的作业实例
    List<String> nodeList = JSON.parseArray(workflowConfig.getNodeList(), String.class);
    List<WorkInstanceEntity> workInstances = new ArrayList<>();
    for (String workId : nodeList) {
      WorkInstanceEntity metaInstance =
          WorkInstanceEntity.builder()
              .workId(workId)
              .instanceType(InstanceType.MANUAL)
              .status(InstanceStatus.PENDING)
              .workflowInstanceId(workflowInstance.getId())
              .build();
      workInstances.add(metaInstance);
    }
    workInstanceRepository.saveAllAndFlush(workInstances);

    // 获取startNode
    List<String> startNodes = JSON.parseArray(workflowConfig.getDagStartList(), String.class);
    List<String> endNodes = JSON.parseArray(workflowConfig.getDagEndList(), String.class);
    List<List<String>> nodeMapping =
        JSON.parseObject(
            workflowConfig.getNodeMapping(), new TypeReference<List<List<String>>>() {});

    // 封装event推送时间，开始执行任务
    // 异步触发工作流
    List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
    for (WorkEntity work : startNodeWorks) {
      WorkflowRunEvent metaEvent =
          WorkflowRunEvent.builder()
              .workId(work.getId())
              .workName(work.getName())
              .dagEndList(endNodes)
              .dagStartList(startNodes)
              .flowInstanceId(workflowInstance.getId())
              .nodeMapping(nodeMapping)
              .nodeList(nodeList)
              .tenantId(TENANT_ID.get())
              .userId(USER_ID.get())
              .build();
      eventPublisher.publishEvent(metaEvent);
    }

    return workflowInstance.getId();
  }

  public WofQueryRunWorkInstancesRes queryRunWorkInstances(String workflowInstanceId) {

    // 查询工作流实例
    Optional<WorkflowInstanceEntity> workflowInstanceEntityOptional =
        workflowInstanceRepository.findById(workflowInstanceId);
    if (!workflowInstanceEntityOptional.isPresent()) {
      throw new SparkYunException("工作流实例不存在");
    }
    WorkflowInstanceEntity workflowInstance = workflowInstanceEntityOptional.get();

    // 查询作业实例列表
    List<WorkInstanceEntity> workInstances =
        workInstanceRepository.findAllByWorkflowInstanceId(workflowInstanceId);
    List<WorkInstanceInfo> instanceInfos =
        workflowMapper.workInstanceEntityListToWorkInstanceInfoList(workInstances);

    // 封装返回对象
    return WofQueryRunWorkInstancesRes.builder()
        .flowStatus(workflowInstance.getStatus())
        .flowRunLog(workflowInstance.getRunLog())
        .workInstances(instanceInfos)
        .build();
  }

  public WofGetWorkflowRes getWorkflow(String workflowId) {

    WorkflowEntity workflow = getWorkflowEntity(workflowId);

    WorkflowConfigEntity workflowConfig =
        workflowConfigRepository.findById(workflow.getConfigId()).get();

    WofGetWorkflowRes wofGetWorkflowRes = new WofGetWorkflowRes();
    wofGetWorkflowRes.setWebConfig(JSON.parse(workflowConfig.getWebConfig()));

    return wofGetWorkflowRes;
  }

  /** 导出作业或者作业流 */
  public void exportWorks(WofExportWorkflowReq wofExportWorkflowReq, HttpServletResponse response) {

    // 导出作业流
    WorkflowEntity workflow = new WorkflowEntity();
    WorkflowConfigEntity workflowConfig = new WorkflowConfigEntity();
    if (!Strings.isEmpty(wofExportWorkflowReq.getWorkflowId())) {
      workflow = workflowRepository.findById(wofExportWorkflowReq.getWorkflowId()).get();
      workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

      List<String> workIds =
          workRepository.findAllByWorkflowId(wofExportWorkflowReq.getWorkflowId()).stream()
              .map(WorkEntity::getId)
              .collect(Collectors.toList());
      wofExportWorkflowReq.setWorkIds(workIds);
    }

    // 导出作业
    List<WorkExportInfo> works = new ArrayList<>();
    if (wofExportWorkflowReq.getWorkIds() != null && !wofExportWorkflowReq.getWorkIds().isEmpty()) {
      wofExportWorkflowReq
          .getWorkIds()
          .forEach(
              workId -> {
                WorkEntity work = workRepository.findById(workId).get();
                WorkConfigEntity workConfig =
                    workConfigRepository.findById(work.getConfigId()).get();
                WorkExportInfo metaExportInfo =
                    WorkExportInfo.builder().workConfig(workConfig).work(work).build();
                works.add(metaExportInfo);
              });
    }

    WorkflowExportInfo workflowExportInfo =
        WorkflowExportInfo.builder()
            .workflowConfig(workflowConfig)
            .workflow(workflow)
            .works(works)
            .build();

    // 导出的文件名
    String exportFileName;
    try {
      if (!Strings.isEmpty(wofExportWorkflowReq.getWorkflowId())) {
        exportFileName =
            URLEncoder.encode(workflow.getName() + "_all", String.valueOf(StandardCharsets.UTF_8));
      } else if (works.size() > 1) {
        exportFileName =
            URLEncoder.encode(
                workflow.getName() + "_" + works.size() + "_works",
                String.valueOf(StandardCharsets.UTF_8));
      } else if (works.size() == 1) {
        exportFileName =
            URLEncoder.encode(
                works.get(0).getWork().getName(), String.valueOf(StandardCharsets.UTF_8));
      } else {
        throw new SparkYunException("作业数量为空不能导出");
      }
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage());
      throw new SparkYunException(e.getMessage());
    }

    // 生成json并导出
    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.setHeader(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + exportFileName + ".json");

    // 导出
    PrintWriter out;
    try {
      out = response.getWriter();
      out.write(JSON.toJSONString(workflowExportInfo));
      out.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException(e.getMessage());
    }
  }

  public void importWorks(MultipartFile workFile, String workflowId) {

    // 解析文本
    String exportWorkflowInfoStr;
    try {
      exportWorkflowInfoStr = new String(workFile.getBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException(e.getMessage());
    }

    // 获取对象
    WorkflowExportInfo workflowExportInfo =
        JSON.parseObject(exportWorkflowInfoStr, WorkflowExportInfo.class);

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
      throw new SparkYunException("作业导入不能没有作业流id");
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

      workIdMapping.forEach(
          (oldWorkId, newWorkId) -> {
            workflowConfigEntity.setDagStartList(
                workflowConfigEntity.getDagStartList().replace(oldWorkId, newWorkId));
            workflowConfigEntity.setNodeMapping(
                workflowConfigEntity.getNodeMapping().replace(oldWorkId, newWorkId));
            workflowConfigEntity.setDagEndList(
                workflowConfigEntity.getDagEndList().replace(oldWorkId, newWorkId));
            workflowConfigEntity.setNodeList(
                workflowConfigEntity.getNodeList().replace(oldWorkId, newWorkId));
            workflowConfigEntity.setWebConfig(
                workflowConfigEntity.getWebConfig().replace(oldWorkId, newWorkId));
          });
      workflowConfigRepository.save(workflowConfigEntity);
    }
  }

  /** 中止作业流 */
  public void abortFlow(String workflowInstanceId) {

    // 将所有的PENDING作业实例，改为ABORT
    List<WorkInstanceEntity> pendingWorkInstances =
        workInstanceRepository.findAllByWorkflowInstanceIdAndStatus(
            workflowInstanceId, InstanceStatus.PENDING);
    pendingWorkInstances.forEach(
        workInstance -> {
          workInstance.setStatus(InstanceStatus.ABORT);
        });
    workInstanceRepository.saveAll(pendingWorkInstances);

    // 将所有的RUNNING作业实例，改为ABORTING
    List<WorkInstanceEntity> runningWorkInstances =
        workInstanceRepository.findAllByWorkflowInstanceIdAndStatus(
            workflowInstanceId, InstanceStatus.RUNNING);
    runningWorkInstances.forEach(
        workInstance -> {
          workInstance.setStatus(InstanceStatus.ABORTING);
        });
    workInstanceRepository.saveAll(runningWorkInstances);

    // 异步调用中止作业的方法
    CompletableFuture.supplyAsync(
            () -> {
              List<String> exeStatus = new ArrayList<>();
              // 依此中止
              runningWorkInstances.forEach(
                  workInstance -> {
                    exeStatus.add(abortWorkInstance(workInstance));
                  });
              return exeStatus;
            })
        .whenComplete(
            (exeStatus, exception) -> {
              WorkflowInstanceEntity workflowInstance =
                  workflowInstanceRepository.findById(workflowInstanceId).get();
              if (exception != null || exeStatus.contains(InstanceStatus.FAIL)) {
                workflowInstance.setStatus(InstanceStatus.FAIL);
              } else {
                workflowInstance.setStatus(InstanceStatus.ABORT);
              }
              workflowInstance.setExecEndDateTime(new Date());
              workflowInstanceRepository.saveAndFlush(workflowInstance);
            });
  }

  /** 中止作业节点实例. */
  public String abortWorkInstance(WorkInstanceEntity workInstance) {

    WorkEntity workEntity = workRepository.findById(workInstance.getWorkId()).get();
    WorkExecutor workExecutor = workExecutorFactory.create(workEntity.getWorkType());
    workInstance = workInstanceRepository.findById(workInstance.getId()).get();
    try {
      workExecutor.syncAbort(workInstance);

      String submitLog =
          workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "已中止  \n";
      workInstance.setSubmitLog(submitLog);
      workInstance.setStatus(InstanceStatus.ABORT);
      workInstance.setExecEndDateTime(new Date());
      workInstanceRepository.save(workInstance);
      return InstanceStatus.SUCCESS;
    } catch (Exception e) {
      String submitLog =
          workInstance.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "中止失败 \n";
      workInstance.setSubmitLog(submitLog);
      workInstance.setStatus(InstanceStatus.FAIL);
      workInstance.setExecEndDateTime(new Date());
      return InstanceStatus.FAIL;
    }
  }

  /** 中断作业. */
  public void breakFlow(String workInstanceId) {

    // 将作业实例状态改为BREAK
    WorkInstanceEntity workInstance = workInstanceRepository.findById(workInstanceId).get();
    workInstance.setStatus(InstanceStatus.BREAK);
    workInstanceRepository.save(workInstance);
  }

  /** 重跑当前节点. */
  public void runCurrentNode(String workInstanceId) {

    WorkInstanceEntity workInstance = workInstanceRepository.findById(workInstanceId).get();
    WorkflowInstanceEntity workflowInstance =
        workflowInstanceRepository.findById(workInstance.getWorkflowInstanceId()).get();
    WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
    WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();

    CompletableFuture.supplyAsync(
            () -> {
              workflowInstance.setStatus(InstanceStatus.RUNNING);
              workflowInstanceRepository.save(workflowInstance);

              // 同步执行作业
              WorkExecutor workExecutor = workExecutorFactory.create(work.getWorkType());
              WorkRunContext workRunContext =
                  WorkflowUtils.genWorkRunContext(workInstanceId, work, workConfig);
              workExecutor.syncExecute(workRunContext);

              return "OVER";
            })
        .whenComplete(
            (exception, result) -> {
              List<WorkInstanceEntity> workInstances =
                  workInstanceRepository.findAllByWorkflowInstanceId(workflowInstance.getId());
              boolean flowError =
                  workInstances.stream().anyMatch(e -> InstanceStatus.FAIL.equals(e.getStatus()));
              if (flowError) {
                workflowInstance.setStatus(InstanceStatus.FAIL);
              } else {
                workflowInstance.setStatus(InstanceStatus.SUCCESS);
              }
              workflowInstance.setExecEndDateTime(new Date());
              workflowInstanceRepository.saveAndFlush(workflowInstance);
            });
  }

  /** 重跑作业流. */
  public void reRunFlow(String workflowInstanceId) {

    // 先中止作业
    // 将所有的PENDING作业实例，改为ABORT
    List<WorkInstanceEntity> pendingWorkInstances =
        workInstanceRepository.findAllByWorkflowInstanceIdAndStatus(
            workflowInstanceId, InstanceStatus.PENDING);
    pendingWorkInstances.forEach(
        workInstance -> {
          workInstance.setStatus(InstanceStatus.ABORT);
          workInstance.setSparkStarRes(null);
          workInstance.setSubmitLog(null);
          workInstance.setYarnLog(null);
        });
    workInstanceRepository.saveAll(pendingWorkInstances);

    // 将所有的RUNNING作业实例，改为ABORTING
    List<WorkInstanceEntity> runningWorkInstances =
        workInstanceRepository.findAllByWorkflowInstanceIdAndStatus(
            workflowInstanceId, InstanceStatus.RUNNING);
    runningWorkInstances.forEach(
        workInstance -> {
          workInstance.setStatus(InstanceStatus.ABORTING);
        });
    workInstanceRepository.saveAll(runningWorkInstances);

    // 异步调用中止作业的方法
    CompletableFuture.supplyAsync(
            () -> {
              List<String> exeStatus = new ArrayList<>();
              // 依此中止
              runningWorkInstances.forEach(
                  workInstance -> exeStatus.add(abortWorkInstance(workInstance)));
              return exeStatus;
            })
        .whenComplete(
            (exeStatus, exception) -> {

              // 中止完作业后，重新运行

              // 初始化工作流实例状态
              WorkflowInstanceEntity workflowInstance =
                  workflowInstanceRepository.findById(workflowInstanceId).get();
              workflowInstance.setStatus(InstanceStatus.PENDING);
              workflowInstanceRepository.save(workflowInstance);

              // 初始化作业实例状态
              List<WorkInstanceEntity> workInstances =
                  workInstanceRepository.findAllByWorkflowInstanceId(workflowInstanceId);
              workInstances.forEach(
                  e -> {
                    e.setStatus(InstanceStatus.PENDING);
                  });
              workInstanceRepository.saveAll(workInstances);

              // 获取配置工作流配置信息
              WorkflowEntity workflow =
                  workflowRepository.findById(workflowInstance.getFlowId()).get();
              WorkflowConfigEntity workflowConfig =
                  workflowConfigRepository.findById(workflow.getConfigId()).get();

              // 重新执行
              List<String> startNodes =
                  JSON.parseArray(workflowConfig.getDagStartList(), String.class);
              List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
              for (WorkEntity work : startNodeWorks) {
                WorkflowRunEvent metaEvent =
                    WorkflowRunEvent.builder()
                        .workId(work.getId())
                        .workName(work.getName())
                        .dagEndList(JSON.parseArray(workflowConfig.getDagEndList(), String.class))
                        .dagStartList(startNodes)
                        .flowInstanceId(workflowInstance.getId())
                        .nodeMapping(
                            JSON.parseObject(
                                workflowConfig.getNodeMapping(),
                                new TypeReference<List<List<String>>>() {}))
                        .nodeList(JSON.parseArray(workflowConfig.getNodeList(), String.class))
                        .tenantId(TENANT_ID.get())
                        .userId(USER_ID.get())
                        .build();
                eventPublisher.publishEvent(metaEvent);
              }
            });
  }

  public void runAfterFlow(String workInstanceId) {

    WorkInstanceEntity workInstance = workInstanceRepository.findById(workInstanceId).get();
    WorkflowInstanceEntity workflowInstance =
        workflowInstanceRepository.findById(workInstance.getWorkflowInstanceId()).get();

    // 作业流状态改为RUNNING
    workflowInstance.setStatus(InstanceStatus.RUNNING);
    workflowInstanceRepository.save(workflowInstance);

    WorkflowEntity workflow = workflowRepository.findById(workflowInstance.getFlowId()).get();
    WorkflowConfigEntity workflowConfig =
        workflowConfigRepository.findById(workflow.getConfigId()).get();

    // 将下游所有节点，全部状态改为PENDING
    List<List<String>> nodeMapping =
        JSON.parseObject(
            workflowConfig.getNodeMapping(), new TypeReference<List<List<String>>>() {});

    WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
    List<String> afterWorkIds = WorkflowUtils.parseAfterNodes(nodeMapping, work.getId());

    // 将下游所有节点实例，全部状态改为PENDING
    List<WorkInstanceEntity> afterWorkInstances =
        workInstanceRepository.findAllByWorkflowInstanceIdAndWorkIds(
            workInstance.getWorkflowInstanceId(), afterWorkIds);
    afterWorkInstances.forEach(
        e -> {
          e.setStatus(InstanceStatus.PENDING);
          e.setSparkStarRes(null);
          e.setSubmitLog(null);
          e.setYarnLog(null);
        });
    workInstanceRepository.saveAllAndFlush(afterWorkInstances);

    // 推送一次
    WorkflowRunEvent metaEvent =
        WorkflowRunEvent.builder()
            .workId(work.getId())
            .workName(work.getName())
            .dagEndList(JSON.parseArray(workflowConfig.getDagEndList(), String.class))
            .dagStartList(JSON.parseArray(workflowConfig.getDagStartList(), String.class))
            .flowInstanceId(workflowInstance.getId())
            .nodeMapping(nodeMapping)
            .nodeList(JSON.parseArray(workflowConfig.getNodeList(), String.class))
            .tenantId(TENANT_ID.get())
            .userId(USER_ID.get())
            .build();
    eventPublisher.publishEvent(metaEvent);
  }
}
