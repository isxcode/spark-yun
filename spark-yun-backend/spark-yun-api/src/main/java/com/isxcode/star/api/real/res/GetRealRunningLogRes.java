package com.isxcode.star.api.real.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRealRunningLogRes {

    private String id;

    private String runningLog;

    private String status;
}
