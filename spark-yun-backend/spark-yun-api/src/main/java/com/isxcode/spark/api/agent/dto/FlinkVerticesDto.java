package com.isxcode.spark.api.agent.dto;

import lombok.Data;

@Data
public class FlinkVerticesDto {

    private String id;

    private String name;

    private Long maxParallelism;

    private Long parallelism;

    private String status;
}
