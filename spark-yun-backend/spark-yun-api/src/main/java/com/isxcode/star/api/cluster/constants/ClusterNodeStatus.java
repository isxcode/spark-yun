package com.isxcode.star.api.cluster.constants;

/** 引擎节点状态码. */
public interface ClusterNodeStatus {

  /** 未安装. */
  String UN_INSTALL = "UN_INSTALL";

  /** 启动. */
  String RUNNING = "RUNNING";

  /** 待启动. */
  String STOP = "STOP";

  /** 检测中. */
  String CHECKING = "CHECKING";

  /** 启动中. */
  String STARTING = "STARTING";

  /** 停止中. */
  String STOPPING = "STOPPING";

  /** 安装中. */
  String INSTALLING = "INSTALLING";

  /** 卸载中. */
  String REMOVING = "REMOVING";

  /** 待检测. */
  String UN_CHECK = "UN_CHECK";

  /** 检测失败. */
  String CHECK_ERROR = "CHECK_ERROR";

  /** 不可安装. */
  String CAN_NOT_INSTALL = "CAN_NOT_INSTALL";

  /** 可安装. */
  String CAN_INSTALL = "CAN_INSTALL";

  /** 安装失败. */
  String INSTALL_ERROR = "INSTALL_ERROR";
}
