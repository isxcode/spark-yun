package com.isxcode.spark.modules.work.run;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkInfo {

    private String flinkSql;

    private String sparkSql;

    private String printSql;
}
