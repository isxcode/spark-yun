package com.isxcode.star.api.pojos.calculate.engine.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaeQueryEngineRes {

  private String id;

  private String name;

  private String node;

  private String storage;

  private String memory;

  private String checkTime;

  private String status;

  private String comment;
}
