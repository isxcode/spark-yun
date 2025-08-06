package com.isxcode.spark.api.container.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetContainerRunningLogRes {

    private String id;

    private String runningLog;
}
