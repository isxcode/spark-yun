package com.isxcode.star.api.view.pojo.req;

import com.isxcode.star.api.view.pojo.dto.DataSql;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigViewCardReq {

    private String id;

    private Object webConfig;

    private Object exampleData;

    private DataSql dataSql;

    private String datasourceId;

    private String name;
}
