package com.isxcode.spark.api.instance.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.spark.backend.api.base.serializer.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WorkInstanceDto {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDateTime;

    private String status;

    private String type;
}
