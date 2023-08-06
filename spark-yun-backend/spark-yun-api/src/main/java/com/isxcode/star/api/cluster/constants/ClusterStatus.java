package com.isxcode.star.api.cluster.constants;

/** 引擎节点状态码. */
public interface ClusterStatus {

  /** 待配置. */
  String NEW = "NEW";

  /** 待检测. */
  String UN_CHECK = "UN_CHECK";

  /** 已激活. */
  String ACTIVE = "ACTIVE";

  /** 不可用. */
  String NO_ACTIVE = "NO_ACTIVE";

  /** 安装失败. */
  String INSTALL_ERROR = "INSTALL_ERROR";
}
