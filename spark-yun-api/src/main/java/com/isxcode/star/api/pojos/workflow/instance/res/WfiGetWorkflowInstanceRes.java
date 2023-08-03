package com.isxcode.star.api.pojos.workflow.instance.res;

import com.isxcode.star.api.pojos.work.instance.vo.WorkInstanceVo;
import java.util.List;
import lombok.Data;

@Data
public class WfiGetWorkflowInstanceRes {

  private String webConfig;

  private List<WorkInstanceVo> workInstances;
}
