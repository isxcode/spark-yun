package com.isxcode.spark.api.layer.req;

import com.isxcode.spark.backend.api.base.pojos.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageLayerReq extends BasePageRequest {

    private String parentLayerId;
}
