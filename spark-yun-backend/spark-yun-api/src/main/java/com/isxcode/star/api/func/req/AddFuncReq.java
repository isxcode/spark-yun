package com.isxcode.star.api.func.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class AddFuncReq {

    @Schema(title = "udf类型", example = "UDF|UDAF")
    @NotEmpty(message = "udf类别不能为空")
    @Pattern(regexp = "^(UDF|UDAF)$", message = "类型不支持")
    private String type;

    @Schema(title = "udf文件id", example = "sy_48c4304593ea4897b6af999e48685896")
    @NotEmpty(message = "udf文件id不能为空")
    private String fileId;

    @Schema(title = "函数名称", example = "a1")
    @NotEmpty(message = "函数名称不能为空")
    private String funcName;

    @Schema(title = "udf文件类名称", example = "org.example.spark.udf.MyUdf")
    @NotEmpty(message = "类名称不能为空")
    private String className;

    @Schema(title = "函数返回值类型", example = "值列表：string|int|long|double|boolean|date|timestamp")
    @NotEmpty(message = "返回值类型不能为空")
    @Pattern(regexp = "^(string|int|long|double|boolean|date|timestamp)$", message = "类型不支持")
    private String resultType;

    @Schema(title = "备注", example = "描述该函数的定义")
    private String remark;
}
