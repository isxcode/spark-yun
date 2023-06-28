package com.isxcode.star.backend.module.workflow.run;

import lombok.Data;

import java.util.List;

@Data
public class Workflow {

  private String flowConfig;

  private List<Work> works;
}
