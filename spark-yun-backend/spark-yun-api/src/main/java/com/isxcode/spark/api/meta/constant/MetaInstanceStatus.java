package com.isxcode.spark.api.meta.constant;

public interface MetaInstanceStatus {

    /**
     * 采集中.
     */
    String COLLECTING = "COLLECTING";

    /**
     * 中止中.
     */
    String ABORTING = "ABORTING";

    /**
     * 失败.
     */
    String FAIL = "FAIL";

    /**
     * 成功.
     */
    String SUCCESS = "SUCCESS";

    /**
     * 已中止.
     */
    String ABORT = "ABORT";
}
