package com.isxcode.star.api.form.req;

import com.isxcode.star.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
public class QueryDataReq extends BasePageRequest {

    @Schema(title = "表单唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "formId不能为空")
    private String formId;

    @Schema(title = "表单版本号", example = "fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "formVersion不能为空")
    private String formVersion;
}
