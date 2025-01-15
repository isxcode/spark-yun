package com.isxcode.star.api.view.dto;

import lombok.Data;

@Data
public class CardInfo {

    private String id;

    private String name;

    private String type;

    private String datasourceId;

    private DataSql dataSql;

    private Object webConfig;

    private EchartOption exampleData;
}
