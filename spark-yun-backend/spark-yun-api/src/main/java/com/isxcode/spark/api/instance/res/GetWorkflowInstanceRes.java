package com.isxcode.spark.api.instance.res;

import com.isxcode.spark.api.instance.dto.WorkInstanceDto;

import java.util.List;
import lombok.Data;

@Data
public class GetWorkflowInstanceRes {

    private String webConfig;

    private List<WorkInstanceDto> workInstances;
}
