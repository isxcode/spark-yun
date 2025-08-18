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

    private String status;

    private List<String> vertices;
}
