package com.isxcode.star.modules.workflow.service;

import static com.isxcode.star.security.main.WebSecurityConfig.USER_ID;

import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import com.isxcode.star.modules.workflow.entity.WorkflowFavourEntity;
import com.isxcode.star.modules.workflow.repository.WorkflowFavourRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 用户模块接口的业务逻辑. */
@Service
@RequiredArgsConstructor
public class WorkflowFavourBizService {

  private final WorkflowBizService workflowBizService;

  private final WorkflowFavourRepository workflowFavourRepository;

  public void favourWorkflow(String workflowId) {

    workflowBizService.getWorkflowEntity(workflowId);

    // 判断工作流是否被收藏过
    Optional<WorkflowFavourEntity> workflowFavourEntityOptional =
        workflowFavourRepository.findByWorkflowIdAndUserId(workflowId, USER_ID.get());
    if (!workflowFavourEntityOptional.isPresent()) {
      throw new SparkYunException("已收藏");
    }

    WorkflowFavourEntity workflowFavour =
        WorkflowFavourEntity.builder().workflowId(workflowId).userId(USER_ID.get()).build();
    workflowFavourRepository.save(workflowFavour);
  }
}
