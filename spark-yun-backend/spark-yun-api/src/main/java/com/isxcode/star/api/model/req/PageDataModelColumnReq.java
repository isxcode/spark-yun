package com.isxcode.star.api.model.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageDataModelColumnReq extends BasePageRequest {

    @Schema(title = "数据模型id", example = "123")
    @NotEmpty(message = "modelId不能为空")
    private String modelId;
}
