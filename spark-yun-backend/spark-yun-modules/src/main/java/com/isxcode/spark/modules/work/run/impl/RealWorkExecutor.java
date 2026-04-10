package com.isxcode.spark.modules.work.run.impl;

import com.isxcode.spark.api.work.constants.WorkType;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.locker.Locker;
import com.isxcode.spark.common.utils.aes.AesUtils;
import com.isxcode.spark.modules.alarm.service.AlarmService;
import com.isxcode.spark.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.spark.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.spark.modules.cluster.repository.ClusterRepository;
import com.isxcode.spark.modules.datasource.service.DatasourceService;
import com.isxcode.spark.modules.file.repository.FileRepository;
import com.isxcode.spark.modules.file.service.FileService;
import com.isxcode.spark.modules.func.mapper.FuncMapper;
import com.isxcode.spark.modules.func.repository.FuncRepository;
import com.isxcode.spark.modules.meta.service.MetaColumnLineageService;
import com.isxcode.spark.modules.secret.repository.SecretKeyRepository;
import com.isxcode.spark.modules.work.repository.*;
import com.isxcode.spark.modules.work.run.AgentLinkUtils;
import com.isxcode.spark.modules.work.run.WorkRunJobFactory;
import com.isxcode.spark.modules.work.service.WorkService;
import com.isxcode.spark.modules.work.sql.SqlCommentService;
import com.isxcode.spark.modules.work.sql.SqlFunctionService;
import com.isxcode.spark.modules.work.sql.SqlValueService;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import org.springframework.stereotype.Service;

@Service
public class RealWorkExecutor extends SyncWorkExecutor {

    public RealWorkExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
        WorkRepository workRepository, WorkConfigRepository workConfigRepository, Locker locker, AesUtils aesUtils,
        ClusterNodeMapper clusterNodeMapper, DatasourceService datasourceService, IsxAppProperties isxAppProperties,
        FuncRepository funcRepository, FileRepository fileRepository, SqlCommentService sqlCommentService,
        SqlValueService sqlValueService, SqlFunctionService sqlFunctionService, AlarmService alarmService,
        WorkEventRepository workEventRepository, WorkRunJobFactory workRunJobFactory,
        VipWorkVersionRepository vipWorkVersionRepository, WorkService workService,
        SecretKeyRepository secretKeyRepository, FuncMapper funcMapper, AgentLinkUtils agentLinkUtils,
        MetaColumnLineageService metaColumnLineageService, FileService fileService) {

        super(workInstanceRepository, clusterRepository, clusterNodeRepository, workflowInstanceRepository,
            workRepository, workConfigRepository, locker, aesUtils, clusterNodeMapper, datasourceService,
            isxAppProperties, funcRepository, fileRepository, sqlCommentService, sqlValueService, sqlFunctionService,
            alarmService, workEventRepository, workRunJobFactory, vipWorkVersionRepository, workService,
            secretKeyRepository, funcMapper, agentLinkUtils, metaColumnLineageService, fileService);
    }

    @Override
    public String getWorkType() {
        return WorkType.API_SYNC_JDBC;
    }
}
