package com.isxcode.spark.api.agent.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class FlinkTimestampsDto {

    @JsonAlias("INITIALIZING")
    private Long initializing;

    @JsonAlias("RECONCILING")
    private Long reconciling;

    @JsonAlias("CANCELED")
    private Long canceled;

    @JsonAlias("FAILED")
    private Long failed;

    @JsonAlias("FINISHED")
    private Long finished;

    @JsonAlias("SUSPENDED")
    private Long suspended;

    @JsonAlias("RUNNING")
    private Long running;

    @JsonAlias("CANCELLING")
    private Long cancelling;

    @JsonAlias("CREATED")
    private Long created;

    @JsonAlias("FAILING")
    private Long failing;

    @JsonAlias("RESTARTING")
    private Long restarting;
}
