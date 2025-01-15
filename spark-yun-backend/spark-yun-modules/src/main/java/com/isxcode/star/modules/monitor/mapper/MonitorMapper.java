package com.isxcode.star.modules.monitor.mapper;

import com.isxcode.star.api.monitor.dto.NodeMonitorInfo;
import com.isxcode.star.modules.monitor.entity.MonitorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MonitorMapper {

    MonitorEntity nodeMonitorInfoToMonitorEntity(NodeMonitorInfo nodeMonitorInfo);
}
