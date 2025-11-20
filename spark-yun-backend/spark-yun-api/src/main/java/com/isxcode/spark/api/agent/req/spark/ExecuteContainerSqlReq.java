package com.isxcode.spark.api.agent.req.spark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ExecuteContainerSqlReq {

    private String sql;

    private String port;

    private Integer limit;
}
