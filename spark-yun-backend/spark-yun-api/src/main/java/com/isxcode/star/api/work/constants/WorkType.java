package com.isxcode.star.api.work.constants;

/** 作业类型. */
public interface WorkType {

	String QUERY_SPARK_SQL = "SPARK_SQL";

	String EXECUTE_JDBC_SQL = "EXE_JDBC";

	String QUERY_JDBC_SQL = "QUERY_JDBC";

	String DATA_SYNC_JDBC = "DATA_SYNC_JDBC";

	String BASH = "BASH";

	String PYTHON = "PYTHON";

	String SPARK_JAR = "SPARK_JAR";

	/**
	 * spark容器sql.
	 */
	String SPARK_CONTAINER_SQL = "SPARK_CONTAINER_SQL";
}
