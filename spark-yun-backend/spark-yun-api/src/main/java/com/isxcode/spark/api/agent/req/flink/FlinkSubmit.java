package com.isxcode.spark.api.agent.req.flink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlinkSubmit {

    private String entryClass;

    private String appResource;

    private String appName;

    private List<String> programArgs;

    private String deployMode;

    private List<String> appArgs;

    private List<String> jars;

    private String javaHome;

    private String mainClass;

    private String flinkHome;

    private boolean verbose;

    private Map<String, Object> conf;
}
