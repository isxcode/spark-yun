package com.isxcode.star.api.instance.pojos.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WoiQueryInstanceReq extends BasePageRequest {

    @Schema(title = "运行状态")
    private String executeStatus;
}
