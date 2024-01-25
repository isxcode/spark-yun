package com.isxcode.star.api.datasource.pojos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityColumnDto {

	private String componentUuid;

	private Object value;

	private String type;
}
