package com.isxcode.star.api.view.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EchartLegend {

	private long bottom;

	private String left;

	private String orient;
}
