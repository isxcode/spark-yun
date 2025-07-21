package com.isxcode.star.modules.work.run.impl;

import com.isxcode.star.api.work.constants.WorkType;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.locker.Locker;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.common.utils.http.HttpUrlUtils;
import com.isxcode.star.modules.alarm.service.AlarmService;
import com.isxcode.star.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.star.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.star.modules.cluster.repository.ClusterRepository;
import com.isxcode.star.modules.file.repository.FileRepository;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkConfigRepository;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import com.isxcode.star.modules.work.repository.WorkRepository;
import com.isxcode.star.modules.work.run.WorkExecutor;
import com.isxcode.star.modules.work.run.WorkRunContext;
import com.isxcode.star.modules.work.sql.SqlFunctionService;
import com.isxcode.star.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class DatabaseMigratorExecutor extends WorkExecutor {

    private final WorkInstanceRepository workInstanceRepository;

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final WorkRepository workRepository;

    private final WorkConfigRepository workConfigRepository;

    private final IsxAppProperties isxAppProperties;

    private final Locker locker;

    private final HttpUrlUtils httpUrlUtils;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final FileRepository fileRepository;

    public DatabaseMigratorExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
        WorkRepository workRepository, WorkConfigRepository workConfigRepository, IsxAppProperties isxAppProperties,
        Locker locker, HttpUrlUtils httpUrlUtils, ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils,
        FileRepository fileRepository, AlarmService alarmService, SqlFunctionService sqlFunctionService) {

        super(workInstanceRepository, workflowInstanceRepository, alarmService, sqlFunctionService);
        this.workInstanceRepository = workInstanceRepository;
        this.clusterRepository = clusterRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.workRepository = workRepository;
        this.workConfigRepository = workConfigRepository;
        this.isxAppProperties = isxAppProperties;
        this.locker = locker;
        this.httpUrlUtils = httpUrlUtils;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.fileRepository = fileRepository;
    }

    @Override
    public String getWorkType() {
        return WorkType.DB_MIGRATE;
    }

    @Override
    protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {


    }
}
