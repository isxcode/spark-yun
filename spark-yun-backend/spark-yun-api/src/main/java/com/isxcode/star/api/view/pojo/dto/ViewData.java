package com.isxcode.star.api.view.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewData {

	private List<String> xAxis;

	private List<BigDecimal> data;

	private List<PieData> pieData;
}
