package com.isxcode.star.api.monitor.pojos.ao;

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
    private Long diskIoReadSpeed;

    @Schema(title = "网络io读速度")
    private Long networkIoReadSpeed;

    @Schema(title = "磁盘io写速度")
    private Long diskIoWriteSpeed;

    @Schema(title = "网络io写速度")
    private Long networkIoWriteSpeed;

    @Schema(title = "内存使用")
    private Long usedMemorySize;

    @Schema(title = "存储使用")
    private Long usedStorageSize;

    public MonitorLineAo(Long activeNodeSize, String tenantId, LocalDateTime dateTime, Double cpuPercent,
        Long diskIoReadSpeed, Long networkIoReadSpeed, Long diskIoWriteSpeed, Long networkIoWriteSpeed,
        Long usedMemorySize, Long usedStorageSize) {

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
