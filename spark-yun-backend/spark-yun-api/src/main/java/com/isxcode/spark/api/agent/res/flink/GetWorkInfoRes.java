package com.isxcode.spark.api.agent.res.flink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetWorkInfoRes {

    private String appId;

    /**
     * yarn状态的备用状态.
     */
    private String appState;

    /**
     * 以finalState为Flink作业的成功失败判断.
     */
    private String finalState;

    private List<String> vertices;
}
