package com.isxcode.star.modules.work.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.isxcode.star.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.star.api.instance.constants.InstanceStatus;
import com.isxcode.star.api.instance.constants.InstanceType;
import com.isxcode.star.api.work.constants.WorkLog;
import com.isxcode.star.api.work.constants.WorkStatus;
import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.api.work.exceptions.WorkRunException;
import com.isxcode.star.api.work.pojos.dto.*;
import com.isxcode.star.api.work.pojos.req.*;
import com.isxcode.star.api.work.pojos.res.*;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.pojos.BaseResponse;
import com.isxcode.star.common.utils.http.HttpUrlUtils;
import com.isxcode.star.common.utils.http.HttpUtils;
import com.isxcode.star.modules.cluster.entity.ClusterEntity;
import com.isxcode.star.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.mapper.WorkConfigMapper;
import com.isxcode.star.modules.work.mapper.WorkMapper;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkExecutorFactory;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.work.service.WorkConfigService;
import com.isxcode.star.modules.work.service.WorkService;
import com.isxcode.star.modules.workflow.entity.WorkflowConfigEntity;
import com.isxcode.star.modules.workflow.entity.WorkflowEntity;
import com.isxcode.star.modules.workflow.run.WorkflowUtils;
import com.isxcode.star.modules.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkBizService {

    private final WorkExecutorFactory workExecutorFactory;

    private final WorkRepository workRepository;

    private final WorkConfigRepository workConfigRepository;

    private final WorkMapper workMapper;

    private final ClusterRepository calculateEngineRepository;

    private final ClusterNodeRepository engineNodeRepository;

    private final WorkInstanceRepository workInstanceRepository;

    private final WorkConfigBizService workConfigBizService;

    private final WorkflowService workflowService;

    private final WorkService workService;

    private final HttpUrlUtils httpUrlUtils;

    private final WorkConfigService workConfigService;

    private final WorkConfigMapper workConfigMapper;

    public GetWorkRes addWork(AddWorkReq addWorkReq) {

        // 校验作业名的唯一性
        Optional<WorkEntity> workByName =
            workRepository.findByNameAndAndWorkflowId(addWorkReq.getName(), addWorkReq.getWorkflowId());
        if (workByName.isPresent()) {
            throw new IsxAppException("作业名称重复，请重新输入");
        }

        // 将addWorkReq转成workEntity
        WorkEntity work = workMapper.addWorkReqToWorkEntity(addWorkReq);

        // sparkSql要是支持访问hive，必须填写datasourceId
        if (WorkType.QUERY_SPARK_SQL.equals(addWorkReq.getWorkType())) {
            if (addWorkReq.getEnableHive()) {
                if (Strings.isEmpty(addWorkReq.getDatasourceId())) {
                    throw new IsxAppException("开启hive，必须配置hive数据源");
                }
            }
        }

        // python作业和bash作业，必须选择服务器节点
        if (WorkType.PYTHON.equals(addWorkReq.getWorkType()) || WorkType.BASH.equals(addWorkReq.getWorkType())) {
            if (Strings.isEmpty(addWorkReq.getClusterNodeId())) {
                throw new IsxAppException("缺少集群节点配置");
            }
        }

        // sparkSql，数据同步，bash，python，必须配置集群
        if (WorkType.QUERY_SPARK_SQL.equals(addWorkReq.getWorkType())
            || WorkType.DATA_SYNC_JDBC.equals(addWorkReq.getWorkType())
            || WorkType.EXCEL_SYNC_JDBC.equals(addWorkReq.getWorkType())
            || WorkType.BASH.equals(addWorkReq.getWorkType()) || WorkType.PYTHON.equals(addWorkReq.getWorkType())
            || WorkType.SPARK_JAR.equals(addWorkReq.getWorkType())) {
            if (Strings.isEmpty(addWorkReq.getClusterId())) {
                throw new IsxAppException("必须选择计算引擎");
            }
        }

        // 如果jdbc执行和jdbc查询 或者为prql查询，必填数据源
        if (WorkType.EXECUTE_JDBC_SQL.equals(addWorkReq.getWorkType())
            || WorkType.QUERY_JDBC_SQL.equals(addWorkReq.getWorkType())
            || WorkType.PRQL.equals(addWorkReq.getWorkType())) {
            if (Strings.isEmpty(addWorkReq.getDatasourceId())) {
                throw new IsxAppException("数据源是必填项");
            }
        }

        // 初始化作业的配置
        WorkConfigEntity workConfig = new WorkConfigEntity();

        // 配置添加数据源
        workConfig.setDatasourceId(addWorkReq.getDatasourceId());

        // 如果是sparkSql,jdbcQuerySql,jdbcExecuteSql,bash,python作业，需要初始化脚本内容，方便客户使用
        if (WorkType.QUERY_SPARK_SQL.equals(addWorkReq.getWorkType())
            || WorkType.EXECUTE_JDBC_SQL.equals(addWorkReq.getWorkType())
            || WorkType.QUERY_JDBC_SQL.equals(addWorkReq.getWorkType())
            || WorkType.BASH.equals(addWorkReq.getWorkType()) || WorkType.PYTHON.equals(addWorkReq.getWorkType())
            || WorkType.SPARK_CONTAINER_SQL.equals(addWorkReq.getWorkType())
            || WorkType.PRQL.equals(addWorkReq.getWorkType())) {
            workConfigService.initWorkScript(workConfig, addWorkReq.getWorkType());
        }

        // 初始化数据同步分区值
        if (WorkType.DATA_SYNC_JDBC.equals(addWorkReq.getWorkType())
            || (WorkType.EXCEL_SYNC_JDBC.equals(addWorkReq.getWorkType()))) {
            workConfigService.initSyncRule(workConfig);
        }

        // 初始化计算引擎
        if (WorkType.QUERY_SPARK_SQL.equals(addWorkReq.getWorkType())
            || WorkType.DATA_SYNC_JDBC.equals(addWorkReq.getWorkType())
            || WorkType.EXCEL_SYNC_JDBC.equals(addWorkReq.getWorkType())
            || WorkType.BASH.equals(addWorkReq.getWorkType()) || WorkType.PYTHON.equals(addWorkReq.getWorkType())
            || WorkType.SPARK_JAR.equals(addWorkReq.getWorkType())) {
            workConfigService.initClusterConfig(workConfig, addWorkReq.getClusterId(), addWorkReq.getClusterNodeId(),
                addWorkReq.getEnableHive(), addWorkReq.getDatasourceId());
        }

        // 如果jdbc执行和jdbc查询，必填数据源
        if (WorkType.SPARK_CONTAINER_SQL.equals(addWorkReq.getWorkType())) {
            if (Strings.isEmpty(addWorkReq.getContainerId())) {
                throw new IsxAppException("容器是必填项");
            }
            workConfig.setContainerId(addWorkReq.getContainerId());
        }

        // 初始化调度默认值
        workConfigService.initCronConfig(workConfig);

        // 作业配置持久化
        workConfig = workConfigRepository.save(workConfig);

        // 作业信息持久化
        work.setConfigId(workConfig.getId());
        work.setStatus(WorkStatus.UN_PUBLISHED);
        workRepository.save(work);

        // 返回work信息给前端
        return getWork(GetWorkReq.builder().workId(work.getId()).build());
    }

    @Transactional
    public void updateWork(UpdateWorkReq updateWorkReq) {

        WorkEntity work = workService.getWorkEntity(updateWorkReq.getId());
        work = workMapper.updateWorkReqToWorkEntity(updateWorkReq, work);
        workRepository.save(work);
    }

    public Page<PageWorkRes> pageWork(PageWorkReq pageWorkReq) {

        Page<WorkEntity> workPage = workRepository.pageSearchByWorkflowId(pageWorkReq.getSearchKeyWord(),
            pageWorkReq.getWorkflowId(), PageRequest.of(pageWorkReq.getPage(), pageWorkReq.getPageSize()));

        Page<PageWorkRes> map = workPage.map(workMapper::workEntityToPageWorkRes);

        map.getContent().forEach(e -> {
            WorkConfigEntity workConfig = workConfigRepository.findById(e.getConfigId()).get();
            if (!Strings.isEmpty(workConfig.getDatasourceId())) {
                e.setDatasourceId(workConfig.getDatasourceId());
            }

            if (!Strings.isEmpty(workConfig.getClusterConfig())) {
                ClusterConfig clusterConfig = JSON.parseObject(workConfig.getClusterConfig(), ClusterConfig.class);
                if (!Strings.isEmpty(clusterConfig.getClusterId())) {
                    e.setClusterId(clusterConfig.getClusterId());
                }
                if (!Strings.isEmpty(clusterConfig.getClusterNodeId())) {
                    e.setClusterNodeId(clusterConfig.getClusterNodeId());
                }
                e.setEnableHive(clusterConfig.getEnableHive());
            }

        });

        return map;
    }

    public void deleteWork(DeleteWorkReq deleteWorkReq) {

        WorkEntity work = workService.getWorkEntity(deleteWorkReq.getWorkId());

        // 拖拽到DAG中的作业无法删除
        WorkflowEntity workflow = workflowService.getWorkflow(work.getWorkflowId());
        WorkflowConfigEntity workflowConfig = workflowService.getWorkflowConfig(workflow.getConfigId());
        if (workflowConfig.getNodeList() != null) {
            if (JSONArray.parseObject(workflowConfig.getNodeList(), String.class).contains(deleteWorkReq.getWorkId())) {
                throw new IsxAppException("作业在DAG图中无法删除");
            }
        }

        // 发布的作业无法删除
        if (WorkStatus.PUBLISHED.equals(work.getStatus())) {
            throw new IsxAppException("请下线作业");
        }

        // 删除作业配置
        WorkConfigEntity workConfig = workConfigService.getWorkConfigEntity(work.getConfigId());
        workConfigRepository.delete(workConfig);

        // 删除作业
        workRepository.delete(work);
    }

    @Transactional
    public WorkInstanceEntity genWorkInstance(String workId) {

        WorkInstanceEntity workInstanceEntity = new WorkInstanceEntity();
        workInstanceEntity.setWorkId(workId);
        workInstanceEntity.setInstanceType(InstanceType.MANUAL);
        return workInstanceRepository.saveAndFlush(workInstanceEntity);
    }

    /**
     * 提交作业.
     */
    public RunWorkRes runWork(RunWorkReq runWorkReq) {

        // 获取作业信息
        WorkEntity work = workService.getWorkEntity(runWorkReq.getWorkId());

        // 初始化作业实例
        WorkInstanceEntity workInstance = genWorkInstance(work.getId());

        // 获取作业配置
        WorkConfigEntity workConfig = workConfigBizService.getWorkConfigEntity(work.getConfigId());

        // 初始化作业运行上下文
        WorkRunContext workRunContext = WorkflowUtils.genWorkRunContext(workInstance.getId(), work, workConfig);

        // 异步运行作业
        WorkExecutor workExecutor = workExecutorFactory.create(work.getWorkType());
        workExecutor.asyncExecute(workRunContext);

        // 返回作业的实例id
        return RunWorkRes.builder().instanceId(workInstance.getId()).build();
    }

    public GetDataRes getData(GetDataReq getDataReq) {

        // 获取实例
        Optional<WorkInstanceEntity> instanceEntityOptional =
            workInstanceRepository.findById(getDataReq.getInstanceId());
        if (!instanceEntityOptional.isPresent()) {
            throw new IsxAppException("实例不存在");
        }
        WorkInstanceEntity workInstanceEntity = instanceEntityOptional.get();
        if (!InstanceStatus.SUCCESS.equals(workInstanceEntity.getStatus())) {
            throw new IsxAppException("只有成功实例，才可查看数据");
        }
        if (Strings.isEmpty(workInstanceEntity.getResultData())) {
            throw new IsxAppException("请等待作业运行完毕或者对应作业无返回结果");
        }

        if (Strings.isEmpty(workInstanceEntity.getYarnLog())) {
            return new GetDataRes(JSON.parseArray(workInstanceEntity.getResultData()));
        }
        return JSON.parseObject(workInstanceEntity.getResultData(), GetDataRes.class);
    }

    public GetStatusRes getStatus(GetStatusReq getStatusReq) {

        Optional<WorkInstanceEntity> workInstanceEntityOptional =
            workInstanceRepository.findById(getStatusReq.getInstanceId());
        if (!workInstanceEntityOptional.isPresent()) {
            throw new IsxAppException("实例暂未生成请稍后再试");
        }
        WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

        if (Strings.isEmpty(workInstanceEntity.getSparkStarRes())) {
            throw new IsxAppException("请等待作业提交完毕");
        }

        return JSON.parseObject(workInstanceEntity.getSparkStarRes(), GetStatusRes.class);
    }

    /**
     * 中止作业.
     */
    @Transactional
    public void stopJob(StopJobReq stopJobReq) {

        // 通过实例 获取workId
        Optional<WorkInstanceEntity> workInstanceEntityOptional =
            workInstanceRepository.findById(stopJobReq.getInstanceId());
        if (!workInstanceEntityOptional.isPresent()) {
            throw new IsxAppException("实例不存在");
        }
        WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

        if (InstanceStatus.SUCCESS.equals(workInstanceEntity.getStatus())) {
            throw new IsxAppException("已经成功，无法中止");
        }

        if (InstanceStatus.ABORT.equals(workInstanceEntity.getStatus())) {
            throw new IsxAppException("已中止");
        }

        WorkEntity workEntity = workRepository.findById(workInstanceEntity.getWorkId()).get();
        WorkExecutor workExecutor = workExecutorFactory.create(workEntity.getWorkType());

        // 获取中作业id
        if (InstanceType.MANUAL.equals(workInstanceEntity.getInstanceType())) {

            // 普通作业中止
            if (!WorkType.QUERY_SPARK_SQL.equals(workEntity.getWorkType())
                && !WorkType.DATA_SYNC_JDBC.equals(workEntity.getWorkType())
                && !WorkType.EXCEL_SYNC_JDBC.equals(workEntity.getWorkType())
                && !WorkType.SPARK_JAR.equals(workEntity.getWorkType())) {

                workExecutor.syncAbort(workInstanceEntity);

            } else {

                WorkConfigEntity workConfigEntity = workConfigRepository.findById(workEntity.getConfigId()).get();
                String clusterId =
                    JSON.parseObject(workConfigEntity.getClusterConfig(), ClusterConfig.class).getClusterId();
                List<ClusterNodeEntity> allEngineNodes =
                    engineNodeRepository.findAllByClusterIdAndStatus(clusterId, ClusterNodeStatus.RUNNING);
                if (allEngineNodes.isEmpty()) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
                }

                Optional<ClusterEntity> clusterEntityOptional = calculateEngineRepository.findById(clusterId);
                if (!clusterEntityOptional.isPresent()) {
                    throw new IsxAppException("集群不存在");
                }

                // 节点选择随机数
                ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

                if (Strings.isEmpty(workInstanceEntity.getSparkStarRes())) {
                    throw new IsxAppException("还未提交，请稍后再试");
                }

                // 解析实例的状态信息
                RunWorkRes wokRunWorkRes = JSON.parseObject(workInstanceEntity.getSparkStarRes(), RunWorkRes.class);

                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put("appId", wokRunWorkRes.getAppId());
                paramsMap.put("agentType", clusterEntityOptional.get().getClusterType());
                paramsMap.put("sparkHomePath", engineNode.getSparkHomePath());
                paramsMap.put("agentHomePath", engineNode.getAgentHomePath());
                BaseResponse<?> baseResponse = HttpUtils.doGet(
                    httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), "/yag/stopJob"), paramsMap,
                    null, BaseResponse.class);

                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                    throw new IsxAppException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
                }
            }

            // 修改实例状态
            workInstanceEntity.setStatus(InstanceStatus.ABORT);
            String submitLog =
                workInstanceEntity.getSubmitLog() + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "已中止  \n";
            workInstanceEntity.setSubmitLog(submitLog);
            workInstanceEntity.setExecEndDateTime(new Date());
            workInstanceEntity
                .setDuration((System.currentTimeMillis() - workInstanceEntity.getExecStartDateTime().getTime()) / 1000);
            workInstanceRepository.saveAndFlush(workInstanceEntity);
        }
    }

    public GetWorkLogRes getWorkLog(GetYarnLogReq getYarnLogReq) {

        Optional<WorkInstanceEntity> workInstanceEntityOptional =
            workInstanceRepository.findById(getYarnLogReq.getInstanceId());
        if (!workInstanceEntityOptional.isPresent()) {
            throw new IsxAppException("实例暂未生成，请稍后再试");
        }

        WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();
        if (Strings.isEmpty(workInstanceEntity.getYarnLog())) {
            throw new IsxAppException("日志尚未生成,请等待作业运行完毕");
        }
        return GetWorkLogRes.builder().yarnLog(workInstanceEntity.getYarnLog()).build();
    }

    public GetWorkRes getWork(GetWorkReq getWorkReq) {

        WorkEntity work = workService.getWorkEntity(getWorkReq.getWorkId());
        WorkConfigEntity workConfig = workConfigService.getWorkConfigEntity(work.getConfigId());

        GetWorkRes getWorkRes = new GetWorkRes();
        getWorkRes.setName(work.getName());
        getWorkRes.setWorkId(work.getId());
        getWorkRes.setWorkflowId(work.getWorkflowId());

        if (!Strings.isEmpty(workConfig.getDatasourceId())) {
            getWorkRes.setDatasourceId(workConfig.getDatasourceId());
        }

        if (!Strings.isEmpty(workConfig.getScript())) {
            getWorkRes.setScript(workConfig.getScript());
        }

        if (!Strings.isEmpty(workConfig.getCronConfig())) {
            getWorkRes.setCronConfig(JSON.parseObject(workConfig.getCronConfig(), CronConfig.class));
        }

        if (!Strings.isEmpty(workConfig.getSyncWorkConfig())) {
            getWorkRes.setSyncWorkConfig(JSON.parseObject(workConfig.getSyncWorkConfig(), SyncWorkConfig.class));
        }

        if (!Strings.isEmpty(workConfig.getExcelSyncConfig())) {
            getWorkRes.setExcelSyncConfig(JSON.parseObject(workConfig.getExcelSyncConfig(), ExcelSyncConfig.class));
        }

        if (!Strings.isEmpty(workConfig.getApiWorkConfig())) {
            getWorkRes.setApiWorkConfig(JSON.parseObject(workConfig.getApiWorkConfig(), ApiWorkConfig.class));
        }

        if (!Strings.isEmpty(workConfig.getClusterConfig())) {
            getWorkRes.setClusterConfig(JSON.parseObject(workConfig.getClusterConfig(), ClusterConfig.class));
            getWorkRes.getClusterConfig()
                .setSparkConfigJson(JSON.toJSONString(getWorkRes.getClusterConfig().getSparkConfig()));
        }

        if (!Strings.isEmpty(workConfig.getSyncRule())) {
            getWorkRes.setSyncRule(JSON.parseObject(workConfig.getSyncRule(), SyncRule.class));
            if (getWorkRes.getSyncRule().getSqlConfig() != null) {
                getWorkRes.getSyncRule().setSqlConfigJson(JSON.toJSONString(getWorkRes.getSyncRule().getSqlConfig()));
            }
        }

        // 翻译依赖配置
        if (!Strings.isEmpty(workConfig.getLibConfig())) {
            List<String> libId = JSON.parseArray(workConfig.getLibConfig(), String.class);
            getWorkRes.setLibList(libId);
        }

        // 翻译函数配置
        if (!Strings.isEmpty(workConfig.getFuncConfig())) {
            List<String> funcId = JSON.parseArray(workConfig.getFuncConfig(), String.class);
            getWorkRes.setFuncList(funcId);
        }

        // 翻译自定义jar配置
        if (!Strings.isEmpty(workConfig.getJarJobConfig())) {
            JarJobConfig jarJobConfig = JSON.parseObject(workConfig.getJarJobConfig(), JarJobConfig.class);
            getWorkRes.setJarJobConfig(jarJobConfig);
        }

        if (!Strings.isEmpty(workConfig.getContainerId())) {
            getWorkRes.setContainerId(workConfig.getContainerId());
        }

        if (!Strings.isEmpty(workConfig.getAlarmList())) {
            getWorkRes.setAlarmList(JSON.parseArray(workConfig.getAlarmList(), String.class));
        }

        return getWorkRes;
    }

    public ClusterNodeEntity getEngineWork(String calculateEngineId) {

        if (Strings.isEmpty(calculateEngineId)) {
            throw new IsxAppException("作业未配置计算引擎");
        }

        Optional<ClusterEntity> calculateEngineEntityOptional = calculateEngineRepository.findById(calculateEngineId);
        if (!calculateEngineEntityOptional.isPresent()) {
            throw new IsxAppException("计算引擎不存在");
        }

        List<ClusterNodeEntity> allEngineNodes = engineNodeRepository
            .findAllByClusterIdAndStatus(calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new IsxAppException("计算引擎无可用节点，请换一个计算引擎");
        }
        return allEngineNodes.get(0);
    }

    public GetSubmitLogRes getSubmitLog(GetSubmitLogReq getSubmitLogReq) {

        Optional<WorkInstanceEntity> workInstanceEntityOptional =
            workInstanceRepository.findById(getSubmitLogReq.getInstanceId());
        if (!workInstanceEntityOptional.isPresent()) {
            throw new IsxAppException("请稍后再试");
        }
        WorkInstanceEntity workInstanceEntity = workInstanceEntityOptional.get();

        return GetSubmitLogRes.builder().log(workInstanceEntity.getSubmitLog()).status(workInstanceEntity.getStatus())
            .build();
    }

    public void renameWork(RenameWorkReq wokRenameWorkReq) {

        WorkEntity workEntity = workService.getWorkEntity(wokRenameWorkReq.getWorkId());

        workEntity.setName(wokRenameWorkReq.getWorkName());

        workRepository.save(workEntity);
    }

    public void copyWork(CopyWorkReq wokCopyWorkReq) {

        // 获取作业信息
        WorkEntity work = workService.getWorkEntity(wokCopyWorkReq.getWorkId());

        // 获取作业配置
        WorkConfigEntity workConfig = workConfigBizService.getWorkConfigEntity(work.getConfigId());

        // 初始化作业配置
        workConfig.setId(null);
        workConfig.setVersionNumber(null);
        workConfig = workConfigRepository.save(workConfigRepository.save(workConfig));

        // 初始化作业
        work.setTopIndex(null);
        work.setConfigId(workConfig.getId());
        work.setName(wokCopyWorkReq.getWorkName());
        work.setVersionNumber(null);
        workRepository.save(work);
    }

    public void topWork(TopWorkReq topWorkReq) {

        WorkEntity work = workService.getWorkEntity(topWorkReq.getWorkId());

        // 获取作业最大的
        Integer maxTopIndex = workRepository.findWorkflowMaxTopIndex(work.getWorkflowId());

        if (maxTopIndex == null) {
            work.setTopIndex(1);
        } else {
            work.setTopIndex(maxTopIndex + 1);
        }
        workRepository.save(work);
    }

    public void saveSyncWorkConfig(SaveSyncWorkConfigReq saveSyncWorkConfigReq) {

        WorkEntity work = workService.getWorkEntity(saveSyncWorkConfigReq.getWorkId());
        WorkConfigEntity workConfig = workConfigService.getWorkConfigEntity(work.getConfigId());

        workConfig.setSyncWorkConfig(JSON.toJSONString(saveSyncWorkConfigReq));

        workConfigRepository.save(workConfig);
    }

    public GetSyncWorkConfigRes getSyncWorkConfig(GetSyncWorkConfigReq getSyncWorkConfigReq) {

        WorkEntity work = workService.getWorkEntity(getSyncWorkConfigReq.getWorkId());
        WorkConfigEntity workConfig = workConfigService.getWorkConfigEntity(work.getConfigId());

        SyncWorkConfig syncWorkConfig = JSON.parseObject(workConfig.getSyncWorkConfig(), SyncWorkConfig.class);

        return workConfigMapper.syncWorkConfigToGetSyncWorkConfigRes(syncWorkConfig);
    }
}
