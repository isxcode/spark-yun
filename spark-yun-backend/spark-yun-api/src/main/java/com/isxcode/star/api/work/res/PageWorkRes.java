package com.isxcode.star.api.work.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageWorkRes {

    private String remark;

    private String name;

    private String id;

    private String workType;

    private String createDateTime;

    private String status;

    private String corn;

    private String datasourceId;

    private String clusterId;

    private String clusterNodeId;

    private Boolean enableHive;

    private String configId;
}
