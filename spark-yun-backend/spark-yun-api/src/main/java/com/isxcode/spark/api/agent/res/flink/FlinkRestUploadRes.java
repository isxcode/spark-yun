package com.isxcode.spark.api.agent.res.flink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlinkRestUploadRes {

    private String filename;

    private String status;
}
