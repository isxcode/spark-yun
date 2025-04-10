package com.isxcode.star.api.monitor.ao;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateMinuteSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitorLineAo {

    @Schema(title = "激活节点个数")
    private Long activeNodeSize;

    @Schema(title = "租户id")
    private String tenantId;

    @Schema(title = "当前时间")
    @JsonSerialize(using = LocalDateMinuteSerializer.class)
    private LocalDateTime dateTime;

    @Schema(title = "cpu占用率")
    private Double cpuPercent;

    @Schema(title = "磁盘io读速度")
    private Double diskIoReadSpeed;

    @Schema(title = "网络io读速度")
    private Double networkIoReadSpeed;

    @Schema(title = "磁盘io写速度")
    private Double diskIoWriteSpeed;

    @Schema(title = "网络io写速度")
    private Double networkIoWriteSpeed;

    @Schema(title = "内存使用")
    private Double usedMemorySize;

    @Schema(title = "存储使用")
    private Double usedStorageSize;

    public MonitorLineAo(Long activeNodeSize, String tenantId, LocalDateTime dateTime, Double cpuPercent,
        Double diskIoReadSpeed, Double networkIoReadSpeed, Double diskIoWriteSpeed, Double networkIoWriteSpeed,
        Double usedMemorySize, Double usedStorageSize) {

        this.activeNodeSize = activeNodeSize;
        this.tenantId = tenantId;
        this.dateTime = dateTime;
        this.cpuPercent = cpuPercent;
        this.diskIoReadSpeed = diskIoReadSpeed;
        this.networkIoReadSpeed = networkIoReadSpeed;
        this.diskIoWriteSpeed = diskIoWriteSpeed;
        this.networkIoWriteSpeed = networkIoWriteSpeed;
        this.usedMemorySize = usedMemorySize;
        this.usedStorageSize = usedStorageSize;
    }
}
