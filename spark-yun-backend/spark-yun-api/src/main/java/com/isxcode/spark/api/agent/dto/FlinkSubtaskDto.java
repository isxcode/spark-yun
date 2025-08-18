package com.isxcode.spark.api.agent.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class FlinkSubtaskDto {

    @JsonAlias("taskmanager-id")
    private String taskmanagerId;
}
