package com.isxcode.star.api.form.pojos.dto;

import lombok.Data;

@Data
public class FomComponentDto {

  private String name;

  private String componentType;

  private String componentKey;

  private boolean isDisplay;

  private boolean isPrimaryKey;

  private String showValue;

  private String valueSql;
}
