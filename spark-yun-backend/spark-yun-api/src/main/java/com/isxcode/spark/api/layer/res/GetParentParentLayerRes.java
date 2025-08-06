package com.isxcode.spark.api.layer.res;

import lombok.Data;


@Data
public class GetParentParentLayerRes {

    private String layerId;

    private String parentLayerId;

    private String parentParentLayerId;
}
