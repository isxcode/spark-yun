package com.isxcode.spark.api.instance.req;

import com.isxcode.spark.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryWorkFlowInstancesReq extends BasePageRequest {

    @Schema(title = "运行状态")
    private String executeStatus;

    @Schema(title = "作业流id")
    private String workflowId;
}
