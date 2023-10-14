package com.isxcode.star.api.agent.pojos.req;

import com.isxcode.star.api.agent.pojos.sup.DataSource;
import com.isxcode.star.api.agent.pojos.sup.MappingData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JDBCSyncPluginReq {
  private DataSource sourceDbInfo;
  private DataSource targetDbInfo;
  private MappingData args;
  private String condition;
  private String overMode;
  private Map<String, String> sparkConfig;

}

