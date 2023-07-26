package com.isxcode.star.backend.module.workflow;

import com.isxcode.star.backend.module.work.WorkExportInfo;
import com.isxcode.star.backend.module.workflow.config.WorkflowConfigEntity;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowExportInfo {

  private WorkflowEntity workflow;

  private WorkflowConfigEntity workflowConfig;

  private List<WorkExportInfo> works;
}
