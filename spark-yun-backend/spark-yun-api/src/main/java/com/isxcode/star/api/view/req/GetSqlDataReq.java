package com.isxcode.star.api.view.req;

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
