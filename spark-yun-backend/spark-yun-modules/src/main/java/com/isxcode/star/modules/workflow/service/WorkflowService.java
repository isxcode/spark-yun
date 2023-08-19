package com.isxcode.star.modules.workflow.service;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.work.entity.WorkInstanceEntity;
import com.isxcode.star.modules.work.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

  private final WorkInstanceRepository workInstanceRepository;

  public WorkInstanceEntity getWorkInstance(String workInstanceId) {

    return workInstanceRepository.findById(workInstanceId).orElseThrow(() -> new IsxAppException("实例不存在"));
  }

}
