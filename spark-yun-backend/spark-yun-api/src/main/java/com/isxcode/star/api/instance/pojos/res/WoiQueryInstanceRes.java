package com.isxcode.star.api.instance.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.backend.api.base.serializer.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WoiQueryInstanceRes {

  private String id;

  private String workName;

  private String workType;

  private String workflowName;

  private String instanceType;

  private String status;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime execStartDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime execEndDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime nextPlanDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime planStartDateTime;
}
