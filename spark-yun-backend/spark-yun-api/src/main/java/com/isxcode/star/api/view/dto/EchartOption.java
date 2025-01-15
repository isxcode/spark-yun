package com.isxcode.star.api.view.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EchartOption {

    private EchartTitle title;

    private EchartTooltip tooltip;

    private EchartToolbox toolbox;

    private EchartLegend legend;

    private List<EchartSeries> series;

    @JsonProperty("xAxis")
    private EchartXAxis xAxis;

    @JsonProperty("yAxis")
    private EchartYAxis yAxis;
}
