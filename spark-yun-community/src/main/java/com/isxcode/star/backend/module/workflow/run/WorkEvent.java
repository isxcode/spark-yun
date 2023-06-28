package com.isxcode.star.backend.module.workflow.run;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@AllArgsConstructor
@Data
public class WorkEvent {

  private Work work;

  private List<List<String>> flowList;

  private List<Work> works;
}
