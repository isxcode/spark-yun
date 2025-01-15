package com.isxcode.star.api.real.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RunRealWorkRes {

    private String status;

    private String errLog;

    private String appId;
}
