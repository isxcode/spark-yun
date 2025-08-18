package com.isxcode.spark.api.agent.res.flink;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.isxcode.spark.api.agent.dto.FlinkTimestampsDto;
import com.isxcode.spark.api.agent.dto.FlinkVerticesDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlinkRestJobRes {

    private String jid;

    private String name;

    private String state;

    @JsonAlias("start-time")
    private Long startTime;

    @JsonAlias("end-time")
    private Long endTime;

    private Long duration;

    private int maxParallelism;

    private Long now;

    private List<FlinkVerticesDto> vertices;

    private FlinkTimestampsDto timestamps;
}
