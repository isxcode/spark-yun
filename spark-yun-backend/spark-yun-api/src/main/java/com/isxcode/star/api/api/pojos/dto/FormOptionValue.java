package com.isxcode.star.api.api.pojos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormOptionValue {

  private String label;

  private String value;
}
