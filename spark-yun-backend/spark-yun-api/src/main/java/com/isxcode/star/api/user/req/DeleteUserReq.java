package com.isxcode.star.api.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeleteUserReq {

    @Schema(title = "用户id", example = "sy_f8402cd43898421687fcc7c8b98a359c")
    @NotEmpty(message = "用户id不能为空")
    private String userId;
}
