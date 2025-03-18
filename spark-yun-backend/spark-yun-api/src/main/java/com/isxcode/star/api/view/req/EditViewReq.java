package com.isxcode.star.api.view.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditViewReq {

    @Schema(title = "大屏id", example = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "大屏名称", example = "名称")
    @NotEmpty(message = "大屏名称不能为空")
    private String name;

    @Schema(title = "备注", example = "123")
    private String remark;
}
