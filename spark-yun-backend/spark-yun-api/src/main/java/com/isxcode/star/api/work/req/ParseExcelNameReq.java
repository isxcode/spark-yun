package com.isxcode.star.api.work.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ParseExcelNameReq {

    @Schema(title = "文件名模板", example = "Book_#{date_to_str(now(),'YYYY-MM-dd')}.xlsx")
    @NotEmpty(message = "文件模板不能为空")
    private String filePattern;
}
