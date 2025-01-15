package com.isxcode.star.api.license.res;

import lombok.Data;

@Data
public class QueryLicenseRes {

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
