package com.isxcode.star.api.form.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
public class AddDataReq {

    @Schema(title = "表单唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "formId不能为空")
    private String formId;

    @Schema(title = "表单版本号", example = "fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "formVersion不能为空")
    private String formVersion;

    @Schema(title = "请求体", example = "")
    @NotEmpty(message = "数据不推荐为空")
    private Map<String, Object> data;
}
