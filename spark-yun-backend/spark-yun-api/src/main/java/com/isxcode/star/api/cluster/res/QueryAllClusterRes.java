package com.isxcode.star.api.cluster.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryAllClusterRes {

    private String id;

    private String name;

    private String checkDateTime;

    private String status;

    private String remark;

    private String clusterType;

    private Boolean defaultCluster;
}
