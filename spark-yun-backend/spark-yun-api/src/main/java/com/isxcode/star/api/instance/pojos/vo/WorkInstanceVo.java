package com.isxcode.star.api.instance.pojos.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WorkInstanceVo {

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime startDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime endDateTime;

  private String status;

  private String type;
}
