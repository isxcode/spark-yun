package com.isxcode.star.api.pojos.work.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RunWorkRes {

  private String message;

  private String log;

  private List<List<String>> data;
}
