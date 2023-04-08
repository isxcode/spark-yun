package com.isxcode.star.api.constants;

/** 引擎节点状态码. */
public interface EngineNodeStatus {

  /**
   * 待检测.
   */
  String UN_CHECK = "UN_CHECK";

  /**
   * 可安装.
   */
  String CAN_INSTALL = "CAN_INSTALL";

  /**
   * 不可安装.
   */
  String CAN_NOT_INSTALL = "CAN_NOT_INSTALL";

  /**
   * 已激活.
   */
  String ACTIVE = "ACTIVE";

  /**
   * 安装失败.
   */
  String INSTALL_ERROR = "INSTALL_ERROR";

  /**
   * 已卸载.
   */
  String UNINSTALLED = "UNINSTALLED";
}
