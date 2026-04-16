package com.isxcode.spark.api.license.res;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CheckLicenseRes {

    private String id;

    private String code;

    private String remark;

    private String startDateTime;

    private String endDateTime;

    private int maxTenantNum;

    private int maxMemberNum;

    private int maxWorkflowNum;

    private String status;
}
