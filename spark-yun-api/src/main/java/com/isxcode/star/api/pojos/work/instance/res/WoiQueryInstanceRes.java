package com.isxcode.star.api.pojos.work.instance.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.star.api.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

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
