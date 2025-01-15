package com.isxcode.star.api.form.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class FormComponentDto {

    private String uuid;

    private String type;

    @NotEmpty(message = "数据字段映射配置不能为空")
    private String formValueCode;

    private String codeType;

    private String label;

    private String placeholder;

    private Boolean disabled;

    private Boolean required;

    private Boolean isColumn;

    private Integer width;

    private String componentType;

    private Boolean valid;

    private String icon;

    private String name;

    private Boolean isPrimaryColumn;

    private List<ComponentOptionDto> options;

    private SwitchOptionDto switchInfo;

    private String colorPicker;

    private String dateType;
}
