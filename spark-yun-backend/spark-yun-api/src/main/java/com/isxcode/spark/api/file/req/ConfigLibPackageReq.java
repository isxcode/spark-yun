package com.isxcode.spark.api.file.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ConfigLibPackageReq {

    @Schema(title = "依赖包id", example = "sy_id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "依赖idList", example = "['sy_1','sy_2']")
    private List<String> fileIdList;
}
