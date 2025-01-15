package com.isxcode.star.api.monitor.res;

import com.isxcode.star.api.monitor.dto.MonitorLineDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetClusterMonitorRes {

    private List<MonitorLineDto> line;
}
