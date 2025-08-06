package com.isxcode.spark.api.view.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSqlDataReq {

    private String sql;

    private String datasourceId;
}
