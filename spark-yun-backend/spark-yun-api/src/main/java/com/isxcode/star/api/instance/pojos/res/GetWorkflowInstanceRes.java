package com.isxcode.star.api.instance.pojos.res;

import com.isxcode.star.api.instance.pojos.dto.WorkInstanceDto;

import java.util.List;
import lombok.Data;

@Data
public class GetWorkflowInstanceRes {

    private String webConfig;

    private List<WorkInstanceDto> workInstances;
}
