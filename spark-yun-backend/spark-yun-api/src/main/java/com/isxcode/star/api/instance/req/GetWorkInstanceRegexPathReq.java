package com.isxcode.star.api.instance.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetWorkInstanceRegexPathReq {

    @NotEmpty(message = "作业实例id不能为空")
    private String workInstanceId;

    @NotEmpty(message = "正则表达式不能为空，默认：XX(\\S+)XX")
    private String regexStr;
}
