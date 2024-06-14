package com.isxcode.star.api.view.pojo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PieData {

	private String name;

	private Double value;
}
