package com.isxcode.star.api.constants;

/** 引擎节点状态码. */
public interface EngineNodeStatus {

  /**
   * 未安装.
   */
  String UN_INSTALL = "UN_INSTALL";

  /**
   * 启动.
   */
  String RUNNING = "RUNNING";

  /**
   * 待启动.
   */
  String STOP = "STOP";

  /**
   * 检测中.
   */
  String CHECKING = "CHECKING";

  /**
   * 启动中.
   */
  String STARTING = "STARTING";

  /**
   * 安装中.
   */
  String INSTALLING = "INSTALLING";

  /**
   * 卸载中.
   */
  String REMOVING = "REMOVING";

  /**
   * 待检测.
   */
  String UN_CHECK = "UN_CHECK";

  /**
   * 检测失败.
   */
  String CHECK_ERROR = "CHECK_ERROR";

  /**
   * 不可安装.
   */
  String CAN_NOT_INSTALL = "CAN_NOT_INSTALL";

  /**
   * 可安装.
   */
  String CAN_INSTALL = "CAN_INSTALL";
}
