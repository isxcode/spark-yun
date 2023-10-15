package com.isxcode.star.api.agent.pojos.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JDBCSyncPluginReq {
  private DataSource sourceDbInfo;
  private DataSource targetDbInfo;
  private HashMap<String, List<String>> columMapping;
  private String condition;
  private String overMode;
  private Map<String, String> sparkConfig;

}

