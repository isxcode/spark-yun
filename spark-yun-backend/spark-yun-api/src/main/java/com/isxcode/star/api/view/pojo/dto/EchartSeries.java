package com.isxcode.star.api.view.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EchartSeries {

  private String name;

  private String type;

  private String radius;

  private List<Object> data;

  private EchartEmphasis emphasis;
}
