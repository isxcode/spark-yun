package com.isxcode.star.api.layer.res;

import lombok.Data;


@Data
public class GetParentParentLayerRes {

    private String layerId;

    private String parentLayerId;

    private String parentParentLayerId;
}
