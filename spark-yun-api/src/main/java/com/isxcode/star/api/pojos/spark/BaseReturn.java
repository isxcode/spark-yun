package com.isxcode.star.api.pojos.spark;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseReturn {
  private List<String> column;

  private List<List<String>> data;
}
