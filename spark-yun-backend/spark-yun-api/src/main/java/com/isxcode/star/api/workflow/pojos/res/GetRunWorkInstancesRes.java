package com.isxcode.star.api.workflow.pojos.res;

import com.isxcode.star.api.workflow.pojos.dto.WorkInstanceInfo;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRunWorkInstancesRes {

  private String flowStatus;

  private String flowRunLog;

  private List<WorkInstanceInfo> workInstances;
}
