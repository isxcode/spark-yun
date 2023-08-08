package com.isxcode.star.backend.api.base.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spark-yun")
public class SparkYunProperties {

  /** 可以让脚本临时复制的目录. */
  private String tmpDir = "/tmp";

  /** 代理默认端口号. */
  private String defaultAgentPort = "30177";

  /** 需要ADMIN权限访问路径. */
  private List<String> adminUrl;

  /** 任何人都可以访问路径. */
  private List<String> anonymousUrl;

  /** aes密钥. */
  private String aesSlat = "spark-yun";

  /** jwt密钥. */
  private String jwtKey = "spark-yun";

  /** jwt超时. */
  private Integer expirationMin = 1440;

  /** 资源目录. */
  private String resourcesPath = "/var/lib/spark-yun";

  /** 系统管理员admin密码. */
  private String adminPasswd = "admin123";

  /** 开启用户操作日志 */
  private boolean logAdvice = false;
}
