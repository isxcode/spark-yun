package com.isxcode.spark.api.work.constants;

/**
 * etl关联方式.
 */
public interface EtlJoinType {

    /**
     * 字段关联.
     */
    String COLUMN_JOIN = "COLUMN_JOIN";

    /**
     * 过滤关联.
     */
    String CONDITION_JOIN = "CONDITION_JOIN";

    /**
     * 自定义关联.
     */
    String CUSTOM_JOIN = "CUSTOM_JOIN";
}
