package com.isxcode.spark.api.work.constants;

/**
 * etl关联.
 */
public interface EtlJoinWay {

    /**
     * 左连接.
     */
    String LEFT_JOIN = "LEFT_JOIN";

    /**
     * 右连接.
     */
    String RIGHT_JOIN = "RIGHT_JOIN";

    /**
     * 内连接.
     */
    String INNER_JOIN = "INNER_JOIN";

    /**
     * 外连接.
     */
    String OUTER_JOIN = "OUTER_JOIN";
}
