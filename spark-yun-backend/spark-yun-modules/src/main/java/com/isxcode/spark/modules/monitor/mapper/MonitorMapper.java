package com.isxcode.spark.modules.monitor.mapper;

import com.isxcode.spark.api.monitor.dto.NodeMonitorInfo;
import com.isxcode.spark.modules.monitor.entity.MonitorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MonitorMapper {

    MonitorEntity nodeMonitorInfoToMonitorEntity(NodeMonitorInfo nodeMonitorInfo);
}
