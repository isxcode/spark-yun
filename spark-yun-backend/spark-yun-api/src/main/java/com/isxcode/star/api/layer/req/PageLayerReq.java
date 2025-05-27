package com.isxcode.star.api.layer.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageLayerReq extends BasePageRequest {

    private String parentLayerId;
}
