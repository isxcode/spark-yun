package com.isxcode.star.api.cluster.pojos.res;

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

  private String checkDateTime;

  private String status;

  private String remark;

  private String clusterType;
}
