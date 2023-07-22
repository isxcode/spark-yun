package com.isxcode.star.api.pojos.workflow.res;

import com.isxcode.star.api.pojos.workflow.dto.WorkInstanceInfo;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WofQueryRunWorkInstancesRes {

  private String flowStatus;

  private String flowRunLog;

  private List<WorkInstanceInfo> workInstances;
}
