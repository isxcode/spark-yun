package com.isxcode.star.api.form.req;

import com.isxcode.star.api.form.dto.FormComponentDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SaveFormConfigReq {

    @Schema(title = "表单唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
    @NotEmpty(message = "formId不能为空")
    private String formId;

    @Schema(title = "组件列表")
    @NotEmpty(message = "组件不能为空")
    private List<FormComponentDto> components;
}
