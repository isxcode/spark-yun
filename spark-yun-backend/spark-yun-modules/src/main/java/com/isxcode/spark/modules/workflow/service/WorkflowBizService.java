package com.isxcode.spark.modules.workflow.service;

import static com.isxcode.spark.api.workflow.constants.WorkflowExternalCallStatus.OFF;
import static com.isxcode.spark.api.workflow.constants.WorkflowExternalCallStatus.ON;
import static com.isxcode.spark.common.config.CommonConfig.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.isxcode.spark.api.instance.constants.InstanceStatus;
import com.isxcode.spark.api.instance.constants.InstanceType;
import com.isxcode.spark.api.instance.ao.WorkflowInstanceAo;
import com.isxcode.spark.api.instance.req.QueryWorkFlowInstancesReq;
import com.isxcode.spark.api.instance.res.QueryWorkFlowInstancesRes;
import com.isxcode.spark.api.work.constants.EventType;
import com.isxcode.spark.api.work.constants.LockerPrefix;
import com.isxcode.spark.api.work.constants.SetMode;
import com.isxcode.spark.api.work.constants.WorkStatus;
import com.isxcode.spark.api.work.dto.CronConfig;
import com.isxcode.spark.api.work.req.GetWorkflowDefaultClusterReq;
import com.isxcode.spark.api.work.res.GetWorkflowDefaultClusterRes;
import com.isxcode.spark.api.workflow.constants.WorkflowExternalCallStatus;
import com.isxcode.spark.api.workflow.constants.WorkflowStatus;
import com.isxcode.spark.api.workflow.dto.WorkInstanceInfo;
import com.isxcode.spark.api.workflow.dto.WorkflowToken;
import com.isxcode.spark.api.workflow.req.*;
import com.isxcode.spark.api.workflow.res.GetRunWorkInstancesRes;
import com.isxcode.spark.api.workflow.res.GetWorkflowRes;
import com.isxcode.spark.api.workflow.res.GetInvokeUrlRes;
import com.isxcode.spark.api.workflow.res.PageWorkflowRes;
import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.exceptions.IsxErrorException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.jwt.JwtUtils;
import com.isxcode.spark.modules.cluster.entity.ClusterEntity;
import com.isxcode.spark.modules.cluster.service.ClusterService;
import com.isxcode.spark.modules.license.repository.LicenseStore;
import com.isxcode.spark.modules.tenant.entity.TenantEntity;
import com.isxcode.spark.modules.tenant.service.TenantService;
import com.isxcode.spark.modules.user.service.UserService;
import com.isxcode.spark.modules.work.entity.WorkConfigEntity;
import com.isxcode.spark.modules.work.entity.WorkEntity;
import com.isxcode.spark.modules.work.entity.WorkExportInfo;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkConfigRepository;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.modules.work.run.WorkExecutorFactory;
import com.isxcode.spark.modules.work.run.WorkRunContext;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.workflow.entity.*;
import com.isxcode.spark.modules.workflow.mapper.WorkflowMapper;
import com.isxcode.spark.modules.workflow.repository.WorkflowConfigRepository;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import com.isxcode.spark.modules.workflow.repository.WorkflowRepository;
import com.isxcode.spark.modules.workflow.repository.WorkflowVersionRepository;
import com.isxcode.spark.modules.workflow.run.WorkflowUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private final ClusterService clusterService;

    private final TenantService tenantService;

    private final LicenseStore licenseStore;

    private final IsxAppProperties isxAppProperties;

    private final UserService userService;

    private final WorkflowVersionRepository workflowVersionRepository;

    private final WorkService workService;

    private final WorkRunJobFactory workRunJobFactory;

    public void addWorkflow(AddWorkflowReq wofAddWorkflowReq) {

        // 判断租户下的作业流上限
        TenantEntity tenant = tenantService.getTenant(TENANT_ID.get());
        long workflowCount = workflowRepository.count();
        if (workflowCount + 1 > tenant.getMaxWorkflowNum()) {
            throw new IsxAppException("超出租户的最大作业流限制");
        }

        // 判断作业流上限是否超过许可证
        if (licenseStore.getLicense() != null) {
            if (workflowCount + 1 > licenseStore.getLicense().getMaxWorkflowNum()) {
                throw new IsxAppException("超出许可证租户的最大作业流限制");
            }
        }

        // 工作流唯一性
        Optional<WorkflowEntity> workflowByName = workflowRepository.findByName(wofAddWorkflowReq.getName());
        if (workflowByName.isPresent()) {
            throw new IsxAppException("作业流名称已重复，请重新输入");
        }

        // 初始化工作流配置
        WorkflowConfigEntity workflowConfig = new WorkflowConfigEntity();
        workflowConfig.setCronConfig(
            JSON.toJSONString(CronConfig.builder().setMode(SetMode.SIMPLE).type("ALL").enable(false).build()));
        workflowConfig.setInvokeStatus(WorkflowExternalCallStatus.OFF);
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

        WorkflowEntity workflow =
            workflowMapper.updateWorkflowReqToWorkflowEntity(wofUpdateWorkflowReq, workflowEntityOptional.get());

        workflowRepository.save(workflow);
    }

    public Page<PageWorkflowRes> pageWorkflow(PageWorkflowReq wocQueryWorkflowReq) {

        Page<WorkflowEntity> workflowEntityPage = workflowRepository.searchAll(wocQueryWorkflowReq.getSearchKeyWord(),
            PageRequest.of(wocQueryWorkflowReq.getPage(), wocQueryWorkflowReq.getPageSize()));

        Page<PageWorkflowRes> pageWorkflowRes =
            workflowEntityPage.map(workflowMapper::workflowEntityToQueryWorkflowRes);

        pageWorkflowRes.getContent().forEach(e -> e.setCreateUsername(userService.getUserName(e.getCreateBy())));

        return pageWorkflowRes;
    }

    public void deleteWorkflow(DeleteWorkflowReq deleteWorkflowReq) {

        // 发布的作业需要下线
        WorkflowEntity workflow = workflowService.getWorkflow(deleteWorkflowReq.getWorkflowId());

        if (WorkflowStatus.PUBLISHED.equals(workflow.getStatus())) {
            throw new IsxAppException("请先下线");
        }

        // 删除作业流
        workflowRepository.delete(workflow);

        // 删除作业流配置
        WorkflowConfigEntity workflowConfig = workflowService.getWorkflowConfig(workflow.getConfigId());
        workflowConfigRepository.delete(workflowConfig);

        // 删除所有的作业
        List<WorkEntity> allByWorkflowId = workRepository.findAllByWorkflowId(workflow.getId());
        workRepository.deleteAll(allByWorkflowId);

        // 删除作业配置
        List<String> configIdList = allByWorkflowId.stream().map(WorkEntity::getConfigId).collect(Collectors.toList());
        workConfigRepository.deleteAllById(configIdList);
    }

    /**
     * 运行工作流，返回工作流实例id
     */
    public String runWorkflow(RunWorkflowReq runWorkflowReq) {

        // 判断租户下的作业流上限
        TenantEntity tenant = tenantService.getTenant(TENANT_ID.get());
        long workflowCount = workflowRepository.count();
        if (workflowCount + 1 > tenant.getMaxWorkflowNum()) {
            throw new IsxAppException("超出租户的最大作业流限制");
        }

        // 判断作业流上限是否超过许可证
        if (licenseStore.getLicense() != null) {
            if (workflowCount + 1 > licenseStore.getLicense().getMaxWorkflowNum()) {
                throw new IsxAppException("超出许可证租户的最大作业流限制");
            }
        }

        return workflowService.runWorkflow(runWorkflowReq.getWorkflowId());
    }

    public GetRunWorkInstancesRes getRunWorkInstances(GetRunWorkInstancesReq getRunWorkInstancesReq) {

        // 查询工作流实例
        Optional<WorkflowInstanceEntity> workflowInstanceEntityOptional =
            workflowInstanceRepository.findById(getRunWorkInstancesReq.getWorkflowInstanceId());
        if (!workflowInstanceEntityOptional.isPresent()) {
            throw new IsxAppException("工作流实例不存在");
        }
        WorkflowInstanceEntity workflowInstance = workflowInstanceEntityOptional.get();

        // 查询作业实例列表
        List<WorkInstanceEntity> workInstances =
            workInstanceRepository.findAllByWorkflowInstanceId(getRunWorkInstancesReq.getWorkflowInstanceId());
        List<WorkInstanceInfo> instanceInfos =
            workflowMapper.workInstanceEntityListToWorkInstanceInfoList(workInstances);

        // 封装返回对象
        return GetRunWorkInstancesRes.builder().flowStatus(workflowInstance.getStatus())
            .flowRunLog(workflowInstance.getRunLog()).workInstances(instanceInfos).build();
    }

    public GetWorkflowRes getWorkflow(GetWorkflowReq getWorkflowReq) {

        WorkflowEntity workflow = workflowService.getWorkflow((getWorkflowReq.getWorkflowId()));

        WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();

        GetWorkflowRes wofGetWorkflowRes = new GetWorkflowRes();
        wofGetWorkflowRes.setWebConfig(JSON.parse(workflowConfig.getWebConfig()));

        if (!Strings.isEmpty(workflowConfig.getCronConfig())) {
            wofGetWorkflowRes.setCronConfig(JSON.parseObject(workflowConfig.getCronConfig(), CronConfig.class));
        }

        if (!Strings.isEmpty(workflowConfig.getAlarmList())) {
            wofGetWorkflowRes.setAlarmList(JSON.parseArray(workflowConfig.getAlarmList(), String.class));
        }

        // 启动外部调用处理
        wofGetWorkflowRes.setInvokeStatus(workflowConfig.getInvokeStatus());
        if (ON.equals(workflowConfig.getInvokeStatus())) {
            wofGetWorkflowRes.setInvokeUrl(workflowConfig.getInvokeUrl());
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

        WorkflowExportInfo workflowExportInfo =
            WorkflowExportInfo.builder().workflowConfig(workflowConfig).workflow(workflow).works(works).build();

        // 导出的文件名
        String exportFileName;
        try {
            if (!Strings.isEmpty(wofExportWorkflowReq.getWorkflowId())) {
                exportFileName = URLEncoder.encode(workflow.getName() + "_all", String.valueOf(StandardCharsets.UTF_8));
            } else if (works.size() > 1) {
                exportFileName = URLEncoder.encode(workflow.getName() + "_" + works.size() + "_works",
                    String.valueOf(StandardCharsets.UTF_8));
            } else if (works.size() == 1) {
                exportFileName =
                    URLEncoder.encode(works.get(0).getWork().getName(), String.valueOf(StandardCharsets.UTF_8));
            } else {
                throw new IsxAppException("作业数量为空不能导出");
            }
        } catch (UnsupportedEncodingException e) {
            log.debug(e.getMessage(), e);
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
            log.debug(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    public void importWorkflow(MultipartFile workFile, String workflowId) {

        // 解析文本
        String exportWorkflowInfoStr;
        try {
            exportWorkflowInfoStr = new String(workFile.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
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
     * 作业流中止.
     */
    public void abortFlow(AbortFlowReq abortFlowReq) {

        // 获取作业流实例
        WorkflowInstanceEntity workflowInstance =
            workflowService.getWorkflowInstance(abortFlowReq.getWorkflowInstanceId());

        // 检测不是运行中的作业流无法中止
        if (!InstanceStatus.RUNNING.equals(workflowInstance.getStatus())) {
            throw new IsxAppException("不是运行中状态，无法中止");
        }

        // 获取作业流状态锁，防止异步更新状态异常
        Integer lockerKey = locker.lock(LockerPrefix.WORK_CHANGE_STATUS + abortFlowReq.getWorkflowInstanceId());

        // 将工作流改为ABORTING，防止二次点击
        WorkflowInstanceEntity lastWorkflowInstance =
            workflowInstanceRepository.findById(abortFlowReq.getWorkflowInstanceId()).get();
        lastWorkflowInstance.setStatus(InstanceStatus.ABORTING);
        workflowInstanceRepository.saveAndFlush(lastWorkflowInstance);

        // 将所有的PENDING作业实例，改为ABORT，中断作业运行
        List<WorkInstanceEntity> pendingWorkInstances = workInstanceRepository
            .findAllByWorkflowInstanceIdAndStatus(abortFlowReq.getWorkflowInstanceId(), InstanceStatus.PENDING);
        pendingWorkInstances.forEach(workInstance -> workInstance.setStatus(InstanceStatus.ABORT));
        workInstanceRepository.saveAllAndFlush(pendingWorkInstances);

        // 解锁
        locker.unlock(lockerKey);

        // 获取所有运行中的实例
        List<WorkInstanceEntity> runningWorkInstances = workInstanceRepository
            .findAllByWorkflowInstanceIdAndStatus(abortFlowReq.getWorkflowInstanceId(), InstanceStatus.RUNNING);

        // 异步调用中止作业的方法
        CompletableFuture.supplyAsync(() -> {
            runningWorkInstances.forEach(e -> {
                workService.abortWork(e.getId());
            });
            return "SUCCESS";
        }).whenComplete((exeStatus, exception) -> {

            // 中止成功，修改作业流状态
            WorkflowInstanceEntity workflowInstanceNew =
                workflowInstanceRepository.findById(abortFlowReq.getWorkflowInstanceId()).get();
            workflowInstanceNew.setStatus(InstanceStatus.ABORT);
            workflowInstanceNew.setExecEndDateTime(new Date());
            workflowInstanceNew.setDuration(
                (System.currentTimeMillis() - workflowInstanceNew.getExecStartDateTime().getTime()) / 1000);
            workflowInstanceRepository.saveAndFlush(workflowInstanceNew);
        });
    }

    /**
     * 重跑作业流.
     */
    public void reRunFlow(ReRunFlowReq reRunFlowReq) {

        // 获取作业流实例
        WorkflowInstanceEntity workflowInstance =
            workflowService.getWorkflowInstance(reRunFlowReq.getWorkflowInstanceId());

        // 获取作业流状态锁，防止异步更新状态异常
        Integer lockerKey = locker.lock(LockerPrefix.WORK_CHANGE_STATUS + reRunFlowReq.getWorkflowInstanceId());

        // 将工作流改为RUNNING，防止二次点击
        WorkflowInstanceEntity lastWorkflowInstance =
            workflowInstanceRepository.findById(reRunFlowReq.getWorkflowInstanceId()).get();
        lastWorkflowInstance.setStatus(InstanceStatus.RUNNING);
        workflowInstanceRepository.saveAndFlush(lastWorkflowInstance);

        // 将所有的PENDING作业实例，改为ABORT，中断作业运行
        List<WorkInstanceEntity> pendingWorkInstances = workInstanceRepository
            .findAllByWorkflowInstanceIdAndStatus(reRunFlowReq.getWorkflowInstanceId(), InstanceStatus.PENDING);
        pendingWorkInstances.forEach(workInstance -> workInstance.setStatus(InstanceStatus.ABORT));
        workInstanceRepository.saveAllAndFlush(pendingWorkInstances);

        // 解锁
        locker.unlock(lockerKey);

        // 获取所有运行中的实例
        List<WorkInstanceEntity> runningWorkInstances = workInstanceRepository
            .findAllByWorkflowInstanceIdAndStatus(reRunFlowReq.getWorkflowInstanceId(), InstanceStatus.RUNNING);

        // 异步参数
        Map<String, String> result = new HashMap<>();
        result.put("tenantId", TENANT_ID.get());
        result.put("userId", USER_ID.get());

        // 异步调用中止作业的方法
        CompletableFuture.supplyAsync(() -> {

            runningWorkInstances.forEach(e -> {
                workService.abortWork(e.getId());
            });

            return result;
        }).whenComplete((exeStatus, exception) -> {

            TENANT_ID.set(exeStatus.get("tenantId"));
            USER_ID.set(exeStatus.get("userId"));

            // 初始化工作流实例状态
            workflowInstance.setStatus(InstanceStatus.RUNNING);
            workflowInstance.setExecStartDateTime(new Date());
            workflowInstance.setExecEndDateTime(null);
            workflowInstance.setDuration(null);
            workflowInstanceRepository.saveAndFlush(workflowInstance);

            // 初始化所有实例
            List<WorkInstanceEntity> workInstances =
                workInstanceRepository.findAllByWorkflowInstanceId(reRunFlowReq.getWorkflowInstanceId());
            workInstances.forEach(e -> {
                e.setStatus(InstanceStatus.PENDING);
                e.setEventId(null);
                e.setResultData(null);
                e.setYarnLog(null);
                e.setSubmitLog(null);
                e.setDuration(null);
                e.setExecStartDateTime(null);
                e.setExecEndDateTime(null);
                e.setQuartzHasRun(true);
            });
            workInstanceRepository.saveAllAndFlush(workInstances);

            // 获取配置工作流配置信息
            WorkflowVersionEntity workflowVersion;
            if (InstanceType.MANUAL.equals(workflowInstance.getInstanceType())) {
                WorkflowEntity workflow = workflowRepository.findById(workflowInstance.getFlowId()).get();
                WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();
                workflowVersion = new WorkflowVersionEntity();
                workflowVersion.setDagStartList(workflowConfig.getDagStartList());
                workflowVersion.setDagEndList(workflowConfig.getDagEndList());
                workflowVersion.setNodeMapping(workflowConfig.getNodeMapping());
                workflowVersion.setNodeList(workflowConfig.getNodeList());
            } else {
                workflowVersion = workflowVersionRepository.findById(workflowInstance.getVersionId())
                    .orElseThrow(() -> new IsxAppException("实例不存在"));
            }

            // 重新执行开始节点
            List<String> nodeList = JSON.parseArray(workflowVersion.getNodeList(), String.class);
            List<String> startNodes = JSON.parseArray(workflowVersion.getDagStartList(), String.class);
            List<String> endNodes = JSON.parseArray(workflowVersion.getDagEndList(), String.class);
            List<List<String>> nodeMapping =
                JSON.parseObject(workflowVersion.getNodeMapping(), new TypeReference<List<List<String>>>() {});

            // 把开始节点拉起来
            List<WorkEntity> startNodeWorks = workRepository.findAllByWorkIds(startNodes);
            for (WorkEntity work : startNodeWorks) {

                // 获取作业的配置
                WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();

                // 获取作业实例
                WorkInstanceEntity workInstance =
                    workInstanceRepository.findByWorkIdAndWorkflowInstanceId(work.getId(), workflowInstance.getId());

                // 封装作业执行上下文
                WorkRunContext workRunContext =
                    WorkflowUtils.genWorkRunContext(workInstance.getId(), EventType.WORKFLOW, work, workConfig);
                workRunContext.setDagEndList(endNodes);
                workRunContext.setDagStartList(startNodes);
                workRunContext.setFlowInstanceId(workflowInstance.getId());
                workRunContext.setNodeMapping(nodeMapping);
                workRunContext.setNodeList(nodeList);

                // 提交定时器
                workRunJobFactory.run(workRunContext);
            }
        });
    }

    /**
     * 重跑下游，中止当前节点，初始化下游节点，重跑当前节点
     */
    public void runAfterFlow(RunAfterFlowReq runAfterFlowReq) {

        // 获取作业流实例
        WorkInstanceEntity workInstance = workInstanceRepository.findById(runAfterFlowReq.getWorkInstanceId()).get();
        WorkflowInstanceEntity workflowInstance =
            workflowInstanceRepository.findById(workInstance.getWorkflowInstanceId()).get();

        // 修改作业流实例状态
        workflowInstance.setStatus(InstanceStatus.RUNNING);
        workflowInstanceRepository.save(workflowInstance);

        // 异步调用中止作业的方法
        CompletableFuture.supplyAsync(() -> {
            sparkYunWorkThreadPool.execute(() -> workService.abortWork(workInstance.getId()));
            return "SUCCESS";
        }).whenComplete((exeStatus, exception) -> {
            // 获取作业
            WorkEntity work = workService.getWorkEntity(workInstance.getWorkId());

            // 获取作业的配置
            WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();

            // 获取配置工作流配置信息
            WorkflowVersionEntity workflowVersion;
            if (InstanceType.MANUAL.equals(workflowInstance.getInstanceType())) {
                WorkflowEntity workflow = workflowRepository.findById(workflowInstance.getFlowId()).get();
                WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();
                workflowVersion = new WorkflowVersionEntity();
                workflowVersion.setDagStartList(workflowConfig.getDagStartList());
                workflowVersion.setDagEndList(workflowConfig.getDagEndList());
                workflowVersion.setNodeMapping(workflowConfig.getNodeMapping());
                workflowVersion.setNodeList(workflowConfig.getNodeList());
            } else {
                workflowVersion = workflowVersionRepository.findById(workflowInstance.getVersionId())
                    .orElseThrow(() -> new IsxAppException("实例不存在"));
            }

            // 重新执行开始节点
            List<String> nodeList = JSON.parseArray(workflowVersion.getNodeList(), String.class);
            List<String> endNodes = JSON.parseArray(workflowVersion.getDagEndList(), String.class);
            List<String> startNodes = JSON.parseArray(workflowVersion.getDagStartList(), String.class);
            List<List<String>> nodeMapping =
                JSON.parseObject(workflowVersion.getNodeMapping(), new TypeReference<List<List<String>>>() {});
            List<String> afterWorkIds = WorkflowUtils.parseAfterNodes(nodeMapping, work.getId());

            // 将下游所有节点实例，全部状态改为PENDING
            List<WorkInstanceEntity> afterWorkInstances = workInstanceRepository
                .findAllByWorkflowInstanceIdAndWorkIds(workInstance.getWorkflowInstanceId(), afterWorkIds);

            // 初始化一下实例
            afterWorkInstances.forEach(e -> {
                e.setStatus(InstanceStatus.PENDING);
                e.setEventId(null);
                e.setResultData(null);
                e.setYarnLog(null);
                e.setSubmitLog(null);
                e.setDuration(null);
                e.setExecStartDateTime(null);
                e.setExecEndDateTime(null);
                e.setQuartzHasRun(true);
            });
            workInstanceRepository.saveAllAndFlush(afterWorkInstances);

            // 封装作业执行上下文
            WorkRunContext workRunContext =
                WorkflowUtils.genWorkRunContext(workInstance.getId(), EventType.WORKFLOW, work, workConfig);
            workRunContext.setDagEndList(endNodes);
            workRunContext.setDagStartList(startNodes);
            workRunContext.setFlowInstanceId(workflowInstance.getId());
            workRunContext.setNodeMapping(nodeMapping);
            workRunContext.setNodeList(nodeList);

            // 提交定时器
            workRunJobFactory.run(workRunContext);
        });
    }

    /**
     * 重跑当前，中止当前节点，只跑当前节点，不传递状态，最后检查一次作业流状态，并修改
     */
    public void runCurrentNode(RunCurrentNodeReq runCurrentNodeReq) {

        // 获取作业实例
        WorkInstanceEntity workInstance = workInstanceRepository.findById(runCurrentNodeReq.getWorkInstanceId()).get();
        WorkflowInstanceEntity workflowInstance =
            workflowInstanceRepository.findById(workInstance.getWorkflowInstanceId()).get();
        WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
        WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();

        // 异步调用中止作业的方法
        CompletableFuture.supplyAsync(() -> {
            sparkYunWorkThreadPool.execute(() -> workService.abortWork(workInstance.getId()));
            return "SUCCESS";
        }).whenComplete((exeStatus, exception) -> {

            // 修改作业流状态
            workflowInstance.setStatus(InstanceStatus.RUNNING);
            workflowInstanceRepository.saveAndFlush(workflowInstance);

            // 初始化一下实例
            workInstance.setStatus(InstanceStatus.PENDING);
            workInstance.setEventId(null);
            workInstance.setResultData(null);
            workInstance.setYarnLog(null);
            workInstance.setSubmitLog(null);
            workInstance.setDuration(null);
            workInstance.setExecStartDateTime(null);
            workInstance.setExecEndDateTime(null);
            workInstance.setQuartzHasRun(true);
            workInstanceRepository.saveAndFlush(workInstance);

            // 获取配置工作流配置信息
            WorkflowVersionEntity workflowVersion;
            if (InstanceType.MANUAL.equals(workflowInstance.getInstanceType())) {
                WorkflowEntity workflow = workflowRepository.findById(workflowInstance.getFlowId()).get();
                WorkflowConfigEntity workflowConfig = workflowConfigRepository.findById(workflow.getConfigId()).get();
                workflowVersion = new WorkflowVersionEntity();
                workflowVersion.setDagStartList(workflowConfig.getDagStartList());
                workflowVersion.setDagEndList(workflowConfig.getDagEndList());
                workflowVersion.setNodeMapping(workflowConfig.getNodeMapping());
                workflowVersion.setNodeList(workflowConfig.getNodeList());
            } else {
                workflowVersion = workflowVersionRepository.findById(workflowInstance.getVersionId())
                    .orElseThrow(() -> new IsxAppException("实例不存在"));
            }

            // 重新执行开始节点
            List<String> nodeList = JSON.parseArray(workflowVersion.getNodeList(), String.class);
            List<String> endNodes = JSON.parseArray(workflowVersion.getDagEndList(), String.class);
            List<String> startNodes = JSON.parseArray(workflowVersion.getDagStartList(), String.class);
            List<List<String>> nodeMapping =
                JSON.parseObject(workflowVersion.getNodeMapping(), new TypeReference<List<List<String>>>() {});

            // 封装作业执行上下文
            WorkRunContext workRunContext =
                WorkflowUtils.genWorkRunContext(workInstance.getId(), EventType.WORKFLOW, work, workConfig);
            workRunContext.setDagEndList(endNodes);
            workRunContext.setDagStartList(startNodes);
            workRunContext.setFlowInstanceId(workflowInstance.getId());
            workRunContext.setNodeMapping(nodeMapping);
            workRunContext.setNodeList(nodeList);

            // 提交定时器
            workRunJobFactory.run(workRunContext);
        });
    }

    public void breakFlow(BreakFlowReq breakFlowReq) {

        WorkInstanceEntity workInstance = workflowService.getWorkInstance(breakFlowReq.getWorkInstanceId());

        // 获取作业流状态锁，防止异步更新状态异常
        Integer lockerKey = locker.lock(LockerPrefix.WORK_CHANGE_STATUS + workInstance.getWorkflowInstanceId());

        // 修改实例状态
        workInstance = workflowService.getWorkInstance(breakFlowReq.getWorkInstanceId());
        if (!InstanceStatus.PENDING.equals(workInstance.getStatus())) {
            throw new IsxAppException("只有等待中的作业可以中断");
        }
        workInstance.setStatus(InstanceStatus.BREAK);
        workInstanceRepository.save(workInstance);

        // 解锁
        locker.unlock(lockerKey);
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

    public void invokeWorkflow(InvokeWorkflowReq invokeWorkflowReq) {

        WorkflowEntity workflow = workflowService.getWorkflow(invokeWorkflowReq.getWorkflowId());
        WorkflowConfigEntity workflowConfig = workflowService.getWorkflowConfig(workflow.getConfigId());

        // 只能调用发布后的作业流
        if (!WorkflowStatus.PUBLISHED.equals(workflow.getStatus())) {
            throw new IsxErrorException("未发布的作业流，无法远程调用");
        }

        // 判断是否启动外部调用
        if (OFF.equals(workflowConfig.getInvokeStatus())) {
            throw new IsxErrorException("作业流未开启外部调用");
        }

        // 检验token是否生效
        WorkflowToken workflowToken;
        try {
            workflowToken = JwtUtils.decrypt(isxAppProperties.getJwtKey(), invokeWorkflowReq.getToken(),
                isxAppProperties.getAesSlat(), WorkflowToken.class);
        } catch (Exception e) {
            throw new IsxErrorException("作业流token解析异常:" + e.getMessage());
        }

        if (!"WORKFLOW_INVOKE".equals(workflowToken.getType())) {
            throw new IsxErrorException("非作业流外部调用token");
        }

        if (!invokeWorkflowReq.getWorkflowId().equals(workflowToken.getWorkflowId())) {
            throw new IsxErrorException("token无法调用该作业流");
        }

        // 赋予userId和租户id
        USER_ID.set(workflowToken.getUserId());
        TENANT_ID.set(workflowToken.getTenantId());

        // 调用作业流执行
        workflowService.runInvokeWorkflow(invokeWorkflowReq.getWorkflowId());
    }

    public GetInvokeUrlRes getInvokeUrl(GetInvokeUrlReq getInvokeUrlReq) {

        workflowService.getWorkflow(getInvokeUrlReq.getWorkflowId());

        String invokeUrl = workflowService.getInvokeUrl(getInvokeUrlReq);

        return GetInvokeUrlRes.builder().url(invokeUrl).build();
    }

    public Page<QueryWorkFlowInstancesRes> queryWorkFlowInstances(QueryWorkFlowInstancesReq queryWorkFlowInstancesReq) {

        // 因为是自定义sql，不使用多租户模式
        JPA_TENANT_MODE.set(false);
        Page<WorkflowInstanceAo> workflowInstanceAoPage = workflowInstanceRepository.pageWorkFlowInstances(
            TENANT_ID.get(), queryWorkFlowInstancesReq.getSearchKeyWord(), queryWorkFlowInstancesReq.getExecuteStatus(),
            queryWorkFlowInstancesReq.getWorkflowId(),
            PageRequest.of(queryWorkFlowInstancesReq.getPage(), queryWorkFlowInstancesReq.getPageSize()));

        return workflowInstanceAoPage.map(workflowMapper::wfiWorkflowInstanceAo2WfiQueryWorkFlowInstancesRes);
    }
}
