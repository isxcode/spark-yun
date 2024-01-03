package com.isxcode.star.api.form.pojos.dto;

import lombok.Data;

@Data
public class FomComponentDto {

  private String id;

	private String name;

	private String componentType;

  private String icon;

	private String componentKey;

	private Boolean isDisplay = Boolean.TRUE;

	private Boolean isPrimaryKey = Boolean.FALSE;

	private String showValue;

	private String valueSql;
}
