package com.isxcode.star.api.constants.work.instance;

public interface InstanceStatus {

  /** 准备运行. */
  String PENDING = "PENDING";

  /** 运行中. */
  String RUNNING = "RUNNING";

  /** 运行成功. */
  String SUCCESS = "SUCCESS";

  /** 运行失败. */
  String FAIL = "FAIL";

  /** 已中止. */
  String ABORT = "ABORT";

  /** 中止中. */
  String ABORTING = "ABORTING";
}
