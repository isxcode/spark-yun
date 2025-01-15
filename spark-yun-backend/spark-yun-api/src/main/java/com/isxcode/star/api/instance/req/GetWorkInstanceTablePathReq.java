package com.isxcode.star.api.instance.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GetWorkInstanceTablePathReq {

    @NotEmpty(message = "作业实例id不能为空")
    private String workInstanceId;

    @NotNull(message = "行数不能为空")
    private Integer tableRow;

    @NotNull(message = "列数不能为空")
    private Integer tableCol;
}
