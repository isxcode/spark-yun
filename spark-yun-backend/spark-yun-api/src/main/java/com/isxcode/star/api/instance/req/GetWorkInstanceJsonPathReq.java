package com.isxcode.star.api.instance.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetWorkInstanceJsonPathReq {

    @NotEmpty(message = "作业实例id不能为空")
    private String workInstanceId;
}
