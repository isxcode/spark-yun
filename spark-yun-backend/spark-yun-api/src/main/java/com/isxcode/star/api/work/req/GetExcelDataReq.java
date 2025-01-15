package com.isxcode.star.api.work.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GetExcelDataReq {

    @Schema(title = "资源id", example = "sy_123456789")
    @NotEmpty(message = "资源id不能为空")
    private String fileId;

    @Schema(title = "是否有表头", example = "默认true")
    @NotNull(message = "是否有表头不能为空")
    private boolean hasHeader;
}
