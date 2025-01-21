package com.isxcode.star.api.view.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetViewLinkInfoRes {

    private String viewId;

    private String viewVersion;

    private String tenantId;

    private String viewToken;
}
