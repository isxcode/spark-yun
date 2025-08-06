package com.isxcode.spark.api.model.req;

import com.isxcode.spark.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageDataModelByLayerReq extends BasePageRequest {

    @Schema(title = "分层id", example = "123")
    @NotEmpty(message = "layerId不能为空")
    private String layerId;
}
