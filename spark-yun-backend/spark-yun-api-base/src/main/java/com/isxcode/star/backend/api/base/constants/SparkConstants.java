package com.isxcode.star.backend.api.base.constants;

/** spark默认配置. */
public interface SparkConstants {

  String SPARK_MASTER = "yarn";

  String SPARK_DEPLOY_MODE = "cluster";

  /** 提交作业默认超时时间30秒. */
  Integer SPARK_SUBMIT_TIMEOUT = 30000;
}
