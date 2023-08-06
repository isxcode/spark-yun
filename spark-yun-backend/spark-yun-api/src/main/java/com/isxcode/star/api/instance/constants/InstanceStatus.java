package com.isxcode.star.api.instance.constants;

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

  /** 已中断. */
  String BREAK = "BREAK";

  /** 中止中. */
  String ABORTING = "ABORTING";
}
