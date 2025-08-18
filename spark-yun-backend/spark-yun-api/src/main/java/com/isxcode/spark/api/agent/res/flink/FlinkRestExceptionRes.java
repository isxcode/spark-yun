package com.isxcode.spark.api.agent.res.flink;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlinkRestExceptionRes {

    @JsonAlias("root-exception")
    private String rootException;

    // @JsonAlias("all-exceptions")
    // private List<String> allExceptions;
}
