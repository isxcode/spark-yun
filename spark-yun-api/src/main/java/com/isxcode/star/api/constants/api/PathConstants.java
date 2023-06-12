package com.isxcode.star.api.constants.api;

/** 各个默认指定路径. */
public interface PathConstants {

  /** spark插件的安装路径. */
  String SPARK_MIN_HOME = "spark-min";

  /** 插件文件夹. */
  String PLUGINS_DIR = "plugins";

  /** 依赖文件夹. */
  String LIB_DIR = "lib";

  /** 代理名称. */
  String SPARK_YUN_AGENT_TAR_GZ_NAME = "spark-yun-agent.tar.gz";

  /** 安装脚本. */
  String AGENT_INSTALL_BASH_NAME = "sy-install.sh";

  /** 代理文件命令. */
  String AGENT_PATH_NAME = "spark-yun";

  /** 检测脚本. */
  String AGENT_CHECK_BASH_NAME = "sy-check.sh";

  /** 检测环境脚本. */
  String AGENT_ENV_BASH_FORMAT = "sy-env-%s.sh";

  /** 卸载脚本. */
  String AGENT_REMOVE_BASH_NAME = "sy-remove.sh";

  /** 启动脚本. */
  String AGENT_START_BASH_NAME = "sy-start.sh";

  /** 停止脚本. */
  String AGENT_STOP_BASH_NAME = "sy-stop.sh";
}
