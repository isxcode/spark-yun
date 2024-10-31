package com.isxcode.star.api.instance.pojos.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetWorkInstanceJsonPathReq {

    @NotEmpty(message = "作业流id不能为空")
    private String workInstanceId;
}
