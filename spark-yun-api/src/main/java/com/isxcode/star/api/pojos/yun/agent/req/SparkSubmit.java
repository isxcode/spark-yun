package com.isxcode.star.api.pojos.yun.agent.req;

import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class SparkSubmit {

  private Map<String, String> conf;

  private String appResource;

  private String appName;

  private String master;

  private String deployMode;

  private List<String> appArgs;

  private List<String> jars;

  private String javaHome;

  private String mainClass;

  private String sparkHome;

  private boolean verbose;
}
