package com.isxcode.star.modules.workflow.service;

import static com.isxcode.star.common.config.CommonConfig.USER_ID;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.modules.workflow.entity.WorkflowFavourEntity;
import com.isxcode.star.modules.workflow.repository.WorkflowFavourRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowFavourBizService {

    private final WorkflowService workflowServices;

    private final WorkflowFavourRepository workflowFavourRepository;

    public void favourWorkflow(String workflowId) {

        workflowServices.getWorkflow(workflowId);

        // 判断工作流是否被收藏过
        Optional<WorkflowFavourEntity> workflowFavourEntityOptional =
            workflowFavourRepository.findByWorkflowIdAndUserId(workflowId, USER_ID.get());
        if (!workflowFavourEntityOptional.isPresent()) {
            throw new IsxAppException("已收藏");
        }

        WorkflowFavourEntity workflowFavour =
            WorkflowFavourEntity.builder().workflowId(workflowId).userId(USER_ID.get()).build();
        workflowFavourRepository.save(workflowFavour);
    }
}
