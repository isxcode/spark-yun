package com.isxcode.spark.api.monitor.res;

import com.isxcode.spark.api.monitor.dto.WorkflowInstanceLineDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetInstanceMonitorRes {

    List<WorkflowInstanceLineDto> instanceNumLine;
}
