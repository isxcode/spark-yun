package com.isxcode.star.api.agent.pojos.req;

import java.util.List;
import java.util.Map;

import com.isxcode.star.api.work.pojos.dto.SyncRule;
import com.isxcode.star.api.work.pojos.dto.SyncWorkConfig;
import com.isxcode.star.api.work.pojos.dto.UdfInfo;
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

	private Integer limit;

	private String applicationId;

	private Map<String, String> sparkConfig;

	private SyncWorkConfig syncWorkConfig;

	private SyncRule syncRule;

  private List<UdfInfo> udfList;
}
