package com.isxcode.spark.api.work.constants;

/**
 * etl节点类型.
 */
public interface EtlNodeType {

    /**
     * 数据输入.
     */
    String DATA_INPUT = "DATA_INPUT";

    /**
     * 数据输出.
     */
    String DATA_OUTPUT = "DATA_OUTPUT";

    /**
     * 数据关联.
     */
    String DATA_JOIN = "DATA_JOIN";

    /**
     * 数据过滤.
     */
    String DATA_FILTER = "DATA_FILTER";

    /**
     * 数据合并.
     */
    String DATA_UNION = "DATA_UNION";

    /**
     * 数据转换.
     */
    String DATA_TRANSFORM = "DATA_TRANSFORM";

    /**
     * 新增字段.
     */
    String DATA_ADD_COL = "DATA_ADD_COL";

    /**
     * 数据自定义.
     */
    String DATA_CUSTOM = "DATA_CUSTOM";
}
