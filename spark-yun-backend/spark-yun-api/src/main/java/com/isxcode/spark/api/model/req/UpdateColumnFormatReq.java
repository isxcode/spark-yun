package com.isxcode.spark.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UpdateColumnFormatReq {

    @Schema(title = "数据分层id", example = "123")
    @NotEmpty(message = "id不能为空")
    private String id;

    @Schema(title = "数据标准名称", example = "123")
    @NotEmpty(message = "name不能为空")
    private String name;

    @Schema(title = "数据标准字段类型编码", example = "TEXT、NUMBER")
    @NotEmpty(message = "columnTypeCode不能为空")
    private String columnTypeCode;

    @Schema(title = "数据标准字段具体类型", example = "12")
    private String columnType;

    @Schema(title = "字段规则", example = "12")
    private String columnRule;

    @Schema(title = "备注", example = "备注123")
    private String remark;

    @Schema(title = "默认值", example = "123")
    private String defaultValue;

    @Schema(title = "是否可以为null", example = "ENABLE")
    @NotEmpty(message = "isNull不能为空")
    @Pattern(regexp = "^(ENABLE|DISABLE)$", message = "只可以输入ENABLE或者DISABLE")
    private String isNull;

    @Schema(title = "是否可以重复", example = "ENABLE")
    @NotEmpty(message = "isDuplicate不能为空")
    @Pattern(regexp = "^(ENABLE|DISABLE)$", message = "只可以输入ENABLE或者DISABLE")
    private String isDuplicate;

    @Schema(title = "是否是分区键", example = "ENABLE")
    @NotEmpty(message = "isPartition不能为空")
    @Pattern(regexp = "^(ENABLE|DISABLE)$", message = "只可以输入ENABLE或者DISABLE")
    private String isPartition;

    @Schema(title = "是否是分区键", example = "ENABLE")
    @NotEmpty(message = "isPrimary不能为空")
    @Pattern(regexp = "^(ENABLE|DISABLE)$", message = "只可以输入ENABLE或者DISABLE")
    private String isPrimary;
}
