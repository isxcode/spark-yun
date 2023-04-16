package com.isxcode.star.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "spark-yun")
public class SparkYunProperties {

  /**
   * 压缩包位置.
   */
  private String agentTarGzDir;

  /**
   * bin位置.
   */
  private String agentBinDir;

  /**
   * 代理默认端口号.
   */
  private String defaultAgentPort = "30177";

  /**
   * 需要ADMIN权限访问路径.
   */
  private List<String> adminUrl;

  /**
   * 任何人都可以访问路径.
   */
  private List<String> anonymousUrl;

}
