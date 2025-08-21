package com.isxcode.spark.modules.work.run;

import lombok.Builder;
import lombok.Data;

/**
 * 保存实例中实际运行的脚本.
 */
@Data
@Builder
public class WorkInfo {

    private String script;

    private String printScript;
}
