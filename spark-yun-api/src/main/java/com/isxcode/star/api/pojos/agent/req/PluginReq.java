package com.isxcode.star.api.pojos.agent.req;

import java.util.HashMap;
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

  private String sql;

  private Integer limit = 100;

  private String applicationId;

  private Map<String, String> sparkConfig =
      new HashMap<String, String>() {
        {
          put("spark.executor.memory", "1g");
          put("spark.driver.memory", "1g");
        }
      };
}
