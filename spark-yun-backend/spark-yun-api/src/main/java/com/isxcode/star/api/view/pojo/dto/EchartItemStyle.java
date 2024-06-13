package com.isxcode.star.api.view.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EchartItemStyle {

	private double shadowBlur;

	private double shadowOffsetX;

	private String shadowColor;
}
