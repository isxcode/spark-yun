package com.isxcode.spark.modules.work.service;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.work.entity.WorkEntity;
import com.isxcode.spark.modules.work.entity.WorkEventEntity;
import com.isxcode.spark.modules.work.entity.WorkInstanceEntity;
import com.isxcode.spark.modules.work.repository.WorkEventRepository;
import com.isxcode.spark.modules.work.repository.WorkInstanceRepository;
import com.isxcode.spark.modules.work.repository.WorkRepository;
import com.isxcode.spark.modules.workflow.entity.WorkflowInstanceEntity;
import com.isxcode.spark.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService {

    private final WorkRepository workRepository;

    private final WorkInstanceRepository workInstanceRepository;

    private final WorkflowInstanceRepository workflowInstanceRepository;

    private final WorkEventRepository workEventRepository;

    public WorkEntity getWorkEntity(String workId) {

        return workRepository.findById(workId).orElseThrow(() -> new IsxAppException("作业不存在"));
    }

    public WorkInstanceEntity getWorkInstance(String workInstanceId) {

        return workInstanceRepository.findById(workInstanceId).orElseThrow(() -> new IsxAppException("作业实例不存在"));
    }

    public WorkflowInstanceEntity getWorkFlowInstance(String workflowInstanceId) {

        return workflowInstanceRepository.findById(workflowInstanceId)
            .orElseThrow(() -> new IsxAppException("作业流实例不存在"));
    }

    public WorkEventEntity getWorkEvent(String workEventId) {

        return workEventRepository.findById(workEventId).orElseThrow(() -> new IsxAppException("作业事件不存在"));
    }
}
