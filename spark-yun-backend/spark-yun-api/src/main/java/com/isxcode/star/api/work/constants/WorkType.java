package com.isxcode.star.api.work.constants;

/**
 * 作业类型.
 */
public interface WorkType {

    /**
     * sparkSql执行作业.
     */
    String QUERY_SPARK_SQL = "SPARK_SQL";

    /**
     * jdbcSql执行作业
     */
    String EXECUTE_JDBC_SQL = "EXE_JDBC";

    /**
     * jdbcSql查询作业.
     */
    String QUERY_JDBC_SQL = "QUERY_JDBC";

    /**
     * 数据同步作业.
     */
    String DATA_SYNC_JDBC = "DATA_SYNC_JDBC";

    /**
     * bash脚本作业.
     */
    String BASH = "BASH";

    /**
     * python脚本作业.
     */
    String PYTHON = "PYTHON";

    /**
     * 用户自定义spark作业.
     */
    String SPARK_JAR = "SPARK_JAR";

    /**
     * spark容器sql.
     */
    String SPARK_CONTAINER_SQL = "SPARK_CONTAINER_SQL";

    String API = "API";

    String PRQL = "PRQL";

    String CURL = "CURL";

    /**
     * Excel导入作业.
     */
    String EXCEL_SYNC_JDBC = "EXCEL_SYNC_JDBC";

    /**
     * 实时计算.
     */
    String REAL_WORK = "REAL_WORK";

    /**
     * pyspark作业.
     */
    String PY_SPARK = "PY_SPARK";
}
