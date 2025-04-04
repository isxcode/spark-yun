package com.isxcode.star.api.work.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetWorkflowDefaultClusterRes {

    private String clusterId;

    private String clusterName;
}
