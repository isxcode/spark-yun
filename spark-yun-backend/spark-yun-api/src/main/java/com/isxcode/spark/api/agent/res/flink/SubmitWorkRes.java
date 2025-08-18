package com.isxcode.spark.api.agent.res.flink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitWorkRes {

    public String appId;

    private String webUrl;
}
