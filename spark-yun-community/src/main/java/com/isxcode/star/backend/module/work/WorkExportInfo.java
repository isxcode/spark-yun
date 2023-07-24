package com.isxcode.star.backend.module.work;

import com.isxcode.star.backend.module.work.config.WorkConfigEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkExportInfo {

  private WorkEntity work;

  private WorkConfigEntity workConfig;
}
