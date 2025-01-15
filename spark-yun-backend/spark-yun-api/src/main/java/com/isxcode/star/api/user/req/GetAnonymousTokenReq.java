package com.isxcode.star.api.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetAnonymousTokenReq {

    @Schema(title = "有效天数", description = "1")
    @NotNull(message = "validDay不能为空")
    private Integer validDay;
}
