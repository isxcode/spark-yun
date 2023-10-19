package com.isxcode.star.api.agent.pojos.req;

import java.util.Map;

import com.isxcode.star.api.work.pojos.dto.SyncRule;
import com.isxcode.star.api.work.pojos.dto.SyncWorkConfig;
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
}
