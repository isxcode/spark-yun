package com.isxcode.star.backend.module.work.config.service;

import com.isxcode.star.api.pojos.work.config.req.WocConfigWorkReq;
import com.isxcode.star.backend.module.work.config.entity.WorkConfigEntity;
import com.isxcode.star.backend.module.work.config.mapper.WorkConfigMapper;
import com.isxcode.star.backend.module.work.config.repository.WorkConfigRepository;
import com.isxcode.star.backend.module.work.entity.WorkEntity;
import com.isxcode.star.backend.module.work.repository.WorkRepository;
import com.isxcode.star.common.exception.SparkYunException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WorkConfigBizService {

  private final WorkRepository workRepository;

  private final WorkConfigRepository workConfigRepository;

  private final WorkConfigMapper workConfigMapper;

  public void configWork(WocConfigWorkReq wocConfigWorkReq) {

    Optional<WorkEntity> workEntityOptional = workRepository.findById(wocConfigWorkReq.getWorkId());
    if (!workEntityOptional.isPresent()) {
      throw new SparkYunException("作业不存在");
    }

    Optional<WorkConfigEntity> workConfigEntityOptional = workConfigRepository.findById(workEntityOptional.get().getWorkConfigId());
    if (!workConfigEntityOptional.isPresent()) {
      throw new SparkYunException("作业异常，作业不可用。");
    }

    WorkConfigEntity workConfigEntity = workConfigMapper.wocConfigWorkReqToWorkConfigEntity(wocConfigWorkReq);
    workConfigEntity.setId(workConfigEntityOptional.get().getId());

    workConfigRepository.save(workConfigEntity);
  }

}
