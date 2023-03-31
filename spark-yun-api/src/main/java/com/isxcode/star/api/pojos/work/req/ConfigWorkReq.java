package com.isxcode.star.api.pojos.work.req;

import lombok.Data;

@Data
public class ConfigWorkReq {

  private String workConfigId;

  private String workId;

  private String script;

  private String datasourceId;

  private String engineId;
}
