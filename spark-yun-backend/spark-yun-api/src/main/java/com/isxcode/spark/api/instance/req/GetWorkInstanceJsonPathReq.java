package com.isxcode.spark.api.instance.req;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class GetWorkInstanceJsonPathReq {

    @NotEmpty(message = "作业实例id不能为空")
    private String workInstanceId;
}
