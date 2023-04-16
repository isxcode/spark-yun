package com.isxcode.star.api.pojos.yun.agent.req;

import com.isxcode.star.api.pojos.plugin.req.PluginReq;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class YagExecuteWorkReq {

  /** 启动yarn的作业名称. */
  @NotEmpty(message = "appName不能为空")
  private String appName;

  /** 执行插件的包路径. */
  @NotEmpty(message = "appResourcePath不能为空")
  private String appResourcePath;

  /** 执行插件的class启动类. */
  @NotEmpty(message = "mainClass不能为空")
  private String mainClass;

  /** 插件执行的请求对象. */
  private PluginReq pluginReq;

  /** 代理的安装路径. */
  private String agentHomePath;

  /** spark安装路径. */
  @NotEmpty(message = "sparkHomePath不能为空")
  private String sparkHomePath;

  /** 代理的lib文件夹. */
  @NotEmpty(message = "agentLibPath不能为空")
  private String agentLibPath;
}
