package com.isxcode.star.api.monitor.res;

import com.isxcode.star.api.monitor.dto.WorkflowInstanceLineDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetInstanceMonitorRes {

    List<WorkflowInstanceLineDto> instanceNumLine;
}
