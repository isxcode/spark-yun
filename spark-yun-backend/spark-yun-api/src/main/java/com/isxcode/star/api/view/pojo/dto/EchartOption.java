package com.isxcode.star.api.view.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EchartOption {

	private EchartTitle title;

	private EchartTooltip tooltip;

	private EchartLegend legend;

	private List<EchartSeries> series;

	@JsonProperty("xAxis")
	private EchartXAxis xAxis;

	@JsonProperty("yAxis")
	private EchartYAxis yAxis;
}
