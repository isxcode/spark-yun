package com.isxcode.star.api.pojos.engine.node.res;

import lombok.Data;

@Data
public class EnoQueryNodeRes {

  private String id;

  private String name;

  private String comment;

  private String host;

  private String username;

  private String memory;

  private String cpu;

  private String storage;

  private String status;

  private String checkTime;
}
