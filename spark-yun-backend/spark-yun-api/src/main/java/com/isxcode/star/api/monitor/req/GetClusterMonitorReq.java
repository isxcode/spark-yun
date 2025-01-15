package com.isxcode.star.api.monitor.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class GetClusterMonitorReq {

    @Schema(title = "集群id", example = "sy_123")
    @NotEmpty(message = "clusterId不能为空")
    private String clusterId;

    @Schema(title = "时间类型", example = "THIRTY_MIN|ONE_HOUR|TWO_HOUR|SIX_HOUR|TWELVE_HOUR|ONE_DAY|SEVEN_DAY|THIRTY_DAY")
    @Pattern(regexp = "^(THIRTY_MIN|ONE_HOUR|TWO_HOUR|SIX_HOUR|TWELVE_HOUR|ONE_DAY|SEVEN_DAY|THIRTY_DAY)$",
        message = "timeType不合法")
    @NotEmpty(message = "timeType不能为空")
    private String timeType;
}
