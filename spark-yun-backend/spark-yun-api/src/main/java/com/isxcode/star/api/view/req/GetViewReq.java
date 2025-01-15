package com.isxcode.star.api.view.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetViewReq {

    @Schema(title = "大屏id", example = "sy_123")
    @NotEmpty(message = "id不能为空")
    private String id;
}
