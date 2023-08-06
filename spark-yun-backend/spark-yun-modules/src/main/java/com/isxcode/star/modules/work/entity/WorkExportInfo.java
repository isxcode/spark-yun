package com.isxcode.star.modules.work.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkExportInfo {

  private WorkEntity work;

  private WorkConfigEntity workConfig;
}
