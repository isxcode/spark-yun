package com.isxcode.star.modules.work.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.TypeReference;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.work.constants.SetMode;
import com.isxcode.star.api.work.dto.ClusterConfig;
import com.isxcode.star.api.work.dto.SyncRule;
import com.isxcode.star.api.work.dto.SyncWorkConfig;
import com.isxcode.star.api.work.req.ConfigWorkReq;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatasourceService;
import com.isxcode.star.modules.work.entity.WorkConfigEntity;
import com.isxcode.star.modules.work.entity.WorkEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.service.WorkConfigService;
import com.isxcode.star.modules.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkConfigBizService {

    private final WorkService workService;

    private final WorkConfigService workConfigService;

    private final WorkConfigRepository workConfigRepository;

    private final DatasourceRepository datasourceRepository;

    private final DatasourceService datasourceService;

    public WorkConfigEntity getWorkConfigEntity(String workConfigId) {

        Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workConfigId);
        if (!workConfigEntityOptional.isPresent()) {
            throw new IsxAppException("作业异常，请联系开发者");
        }
        return workConfigEntityOptional.get();
    }

    public void configWork(ConfigWorkReq wocConfigWorkReq) {

        // 先转换sparkConfigJson和sqlConfigJson
        try {
            if (wocConfigWorkReq.getClusterConfig() != null
                && !Strings.isEmpty(wocConfigWorkReq.getClusterConfig().getSparkConfigJson())) {
                wocConfigWorkReq.getClusterConfig()
                    .setSparkConfig(JSON.parseObject(wocConfigWorkReq.getClusterConfig().getSparkConfigJson(),
                        new TypeReference<Map<String, String>>() {}));
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            throw new IsxAppException("集群spark配置json格式不合法");
        }

        try {
            if (wocConfigWorkReq.getSyncRule() != null
                && !Strings.isEmpty(wocConfigWorkReq.getSyncRule().getSqlConfigJson())) {
                wocConfigWorkReq.getSyncRule().setSqlConfig(JSON.parseObject(
                    wocConfigWorkReq.getSyncRule().getSqlConfigJson(), new TypeReference<Map<String, String>>() {}));
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            throw new IsxAppException("数据同步sqlConfig配置json格式不合法");
        }

        WorkEntity work = workService.getWorkEntity(wocConfigWorkReq.getWorkId());

        WorkConfigEntity workConfig = workConfigService.getWorkConfigEntity(work.getConfigId());

        // 用户更新脚本
        if (!Strings.isEmpty(wocConfigWorkReq.getScript())) {
            workConfig.setScript(wocConfigWorkReq.getScript());
        }

        // 用户更新数据同步
        if (wocConfigWorkReq.getSyncWorkConfig() != null) {
            workConfig.setSyncWorkConfig(JSON.toJSONString(wocConfigWorkReq.getSyncWorkConfig()));
        }

        // 用户更新Excel数据同步
        if (wocConfigWorkReq.getExcelSyncConfig() != null) {
            workConfig.setExcelSyncConfig(JSON.toJSONString(wocConfigWorkReq.getExcelSyncConfig()));
        }

        // 用户更新接口调用
        if (wocConfigWorkReq.getApiWorkConfig() != null) {
            workConfig.setApiWorkConfig(JSON.toJSONString(wocConfigWorkReq.getApiWorkConfig()));
        }

        // 用户更新集群配置
        if (wocConfigWorkReq.getClusterConfig() != null) {

            // 如果是等级的模式，需要帮用户默认填充sparkConfig
            if (SetMode.SIMPLE.equals(wocConfigWorkReq.getClusterConfig().getSetMode())) {
                Map<String, String> sparkConfig =
                    workConfigService.initSparkConfig(wocConfigWorkReq.getClusterConfig().getResourceLevel());
                wocConfigWorkReq.getClusterConfig().setSparkConfig(sparkConfig);
            }

            workConfig.setClusterConfig(JSON.toJSONString(wocConfigWorkReq.getClusterConfig()));
        }

        // 用户更新数据源
        if (!Strings.isEmpty(wocConfigWorkReq.getDatasourceId())) {
            workConfig.setDatasourceId(wocConfigWorkReq.getDatasourceId());
        }

        // 用户更新数据同步规则
        if (wocConfigWorkReq.getSyncRule() != null) {
            workConfig.setSyncRule(JSON.toJSONString(wocConfigWorkReq.getSyncRule()));
        }

        // 用户更新调度配置
        if (wocConfigWorkReq.getCronConfig() != null) {
            if (SetMode.ADVANCE.equals(wocConfigWorkReq.getCronConfig().getSetMode())) {
                boolean validExpression = CronExpression.isValidExpression(wocConfigWorkReq.getCronConfig().getCron());
                if (!validExpression) {
                    throw new IsxAppException("Corn表达式异常");
                }
            }
            workConfig.setCronConfig(JSON.toJSONString(wocConfigWorkReq.getCronConfig()));
        }

        // 设置hive.metastore.uris的值,要么是sparkSql支持hive，要是数据同步中有hive数据源
        if (!Strings.isEmpty(workConfig.getClusterConfig())) {
            workConfig.setClusterConfig(JSON.toJSONString(getHiveStoreUri(workConfig)));
        }

        // 设置用户自定义函数
        if (wocConfigWorkReq.getFuncList() != null) {
            if (!wocConfigWorkReq.getFuncList().isEmpty()) {
                workConfig.setFuncConfig(JSONArray.toJSONString(wocConfigWorkReq.getFuncList()));
            } else {
                workConfig.setFuncConfig("[]");
            }
        }

        // 设置用户程序依赖
        if (wocConfigWorkReq.getLibList() != null) {
            if (!wocConfigWorkReq.getLibList().isEmpty()) {
                workConfig.setLibConfig(JSONArray.toJSONString(wocConfigWorkReq.getLibList()));
            } else {
                workConfig.setLibConfig("[]");
            }
        }

        // 设置自定义作业配置信息
        if (wocConfigWorkReq.getJarJobConfig() != null) {
            workConfig.setJarJobConfig(JSON.toJSONString(wocConfigWorkReq.getJarJobConfig()));
        }

        // 设置容器id
        if (wocConfigWorkReq.getContainerId() != null) {
            workConfig.setContainerId(wocConfigWorkReq.getContainerId());
        }

        // 设置容器id
        if (wocConfigWorkReq.getAlarmList() != null) {
            workConfig.setAlarmList(JSON.toJSONString(wocConfigWorkReq.getAlarmList()));
        }

        // 保存配置
        workConfigRepository.save(workConfig);
    }

    public ClusterConfig getHiveStoreUri(WorkConfigEntity workConfig) {

        String hiveMetaStoreUris = null;
        ClusterConfig clusterConfig = JSON.parseObject(workConfig.getClusterConfig(), ClusterConfig.class);

        // 如果是sparkSql作业，且开启hive数据源
        if (clusterConfig.getEnableHive() != null && clusterConfig.getEnableHive()) {
            Optional<DatasourceEntity> datasourceEntity = datasourceRepository.findById(workConfig.getDatasourceId());
            if (datasourceEntity.isPresent()) {
                hiveMetaStoreUris = datasourceEntity.get().getMetastoreUris();
            }
        }

        // 如果是数据同步作业，需要自动装配hive
        if (!Strings.isEmpty(workConfig.getSyncWorkConfig())) {
            SyncWorkConfig syncWorkConfig = JSON.parseObject(workConfig.getSyncWorkConfig(), SyncWorkConfig.class);
            if (DatasourceType.HIVE.equals(syncWorkConfig.getTargetDBType())) {
                DatasourceEntity datasource = datasourceService.getDatasource(syncWorkConfig.getTargetDBId());
                hiveMetaStoreUris = datasource.getMetastoreUris();
            }
            if (DatasourceType.HIVE.equals(syncWorkConfig.getSourceDBType())) {
                DatasourceEntity datasource = datasourceService.getDatasource(syncWorkConfig.getSourceDBId());
                hiveMetaStoreUris = datasource.getMetastoreUris();
            }
        }

        if (hiveMetaStoreUris != null) {
            clusterConfig.getSparkConfig().put("hive.metastore.uris", hiveMetaStoreUris);
        }

        // 根据分区配置修改并发数instances
        if (!Strings.isEmpty(workConfig.getSyncRule()) && !Strings.isEmpty(workConfig.getSyncWorkConfig())) {
            SyncRule syncRule = JSON.parseObject(workConfig.getSyncRule(), SyncRule.class);
            clusterConfig.getSparkConfig().put("spark.executor.instances",
                String.valueOf(syncRule.getNumConcurrency()));
            clusterConfig.getSparkConfig().put("spark.cores.max", String.valueOf(syncRule.getNumConcurrency()));
        }

        return clusterConfig;
    }
}
