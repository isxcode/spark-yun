package com.isxcode.star.api.pojos.work.res;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RunWorkRes {

  private String message;

  private String log;

  private List<List<String>> data;
}
