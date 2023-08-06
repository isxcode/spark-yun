package com.isxcode.star.api.work.constants;

public interface WorkDefault {

  String DEFAULT_SPARK_CONF =
      "{\n"
          + "  \"hive.metastore.uris\": \"\",\n"
          + "  \"spark.executor.memory\":\"1g\",\n"
          + "  \"spark.driver.memory\":\"1g\",\n"
          + "  \"spark.sql.storeAssignmentPolicy\":\"LEGACY\",\n"
          + "  \"spark.sql.legacy.timeParserPolicy\":\"LEGACY\",\n"
          + "  \"spark.hadoop.hive.exec.dynamic.partition\":\"true\",\n"
          + "  \"spark.hadoop.hive.exec.dynamic.partition.mode\":\"nonstrict\"\n"
          + "}";
}
