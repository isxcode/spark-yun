package com.isxcode.spark.api.monitor.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class GetInstanceMonitorReq {

    @Schema(title = "日期时间，例如2020-12-12", example = "2020-12-12")
    @NotNull(message = "localDate不能为空")
    @JsonSerialize(using = LocalDateSerializer.class)
    private Date localDate;
}
