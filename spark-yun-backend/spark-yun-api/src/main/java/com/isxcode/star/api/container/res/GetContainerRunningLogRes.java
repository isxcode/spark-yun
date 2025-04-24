package com.isxcode.star.api.container.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetContainerRunningLogRes {

    private String id;

    private String runningLog;
}
