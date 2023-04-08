package com.isxcode.star.api.pojos.engine.node.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnoInstallAgentRes {

  private String executeLog;
}
