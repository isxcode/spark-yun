package com.isxcode.star.api.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spark-yun")
public class SparkYunProperties {

  /** 压缩包位置. */
  private String agentTarGzDir;

  /** bin位置. */
  private String agentBinDir;

  /** 代理默认端口号. */
  private String defaultAgentPort = "30177";

  /** 需要ADMIN权限访问路径. */
  private List<String> adminUrl;

  /** 任何人都可以访问路径. */
  private List<String> anonymousUrl;

  /** aes密钥. */
  private String aesSlat;

  /** jwt密钥. */
  private String jwtKey;

  /** jwt超时. */
  private Integer expirationMin;

  /** 资源目录. */
  private String resourcesPath;

  /** 系统管理员admin密码. */
  private String adminPasswd;

  /** 开启用户操作日志 */
  private boolean logAdvice;
}
