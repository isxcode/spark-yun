package com.isxcode.star.api.cluster.pojos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnoQueryNodeRes {

  private String id;

  private String name;

  private String remark;

  private String host;

  private String username;

  private String memory;

  private String cpu;

  private String storage;

  private String status;

  private String checkDateTime;

  private String agentHomePath;

  private String agentPort;

  private String hadoopHomePath;

  private String port;

  private String agentLog;
}
