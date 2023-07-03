package com.isxcode.star.api.pojos.workflow.res;

import com.isxcode.star.api.pojos.workflow.dto.WorkInstanceInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WofQueryRunWorkInstancesRes {


  private String flowStatus;

  private List<WorkInstanceInfo> workInstances;
}
