package com.isxcode.star.api.pojos.agent.req;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PluginReq {

  private String executeId;

  private boolean hasReturn;

  private String sql;

  private List<String> columns;

  private Integer page;

  private Integer pageSize;

  private String tableName;

  private Integer limit = 100;

  private String applicationId;

  private String db;

  private String jdbcUrl;

  private String username;

  private String password;

  private String driverClassName;

  private String dbType;

  private String starHome;

  private Map<String, Object> kafkaConfig;

  private Map<String, String> sparkConfig =
      new HashMap<String, String>() {
        {
          put("spark.executor.memory", "1g");
          put("spark.driver.memory", "1g");
        }
      };

  private Map<String, String> yarnJobConfig =
      new HashMap<String, String>() {
        {
          put("appName", "spark-star");
          put("mainClass", "com.isxcode.star.plugin.querysql.Execute");
          put("appResourceName", "spark-query-sql-plugin");
        }
      };
}
