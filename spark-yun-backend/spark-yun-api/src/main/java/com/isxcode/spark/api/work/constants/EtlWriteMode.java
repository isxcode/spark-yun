package com.isxcode.spark.api.work.constants;

/**
 * etl节点写入模式.
 */
public interface EtlWriteMode {

    /**
     * 覆盖.
     */
    String OVERWRITE = "OVERWRITE";

    /**
     * 附加.
     */
    String INTO = "INTO";
}
