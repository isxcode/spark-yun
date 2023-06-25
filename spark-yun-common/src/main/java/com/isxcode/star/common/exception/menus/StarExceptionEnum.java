package com.isxcode.star.common.exception.menus;

import com.isxcode.star.common.exception.AbstractSparkYunExceptionEnum;
import lombok.Getter;

public enum StarExceptionEnum implements AbstractSparkYunExceptionEnum {
  REQUEST_VALUE_EMPTY("50003", "缺少输入参数"),

  SPARK_LAUNCHER_ERROR("50004", "spark发布错误"),

  COMMAND_EXECUTE_ERROR("50005", "命令运行异常"),
  ;

  @Getter private final String code;

  @Getter private final String msg;

  StarExceptionEnum(String code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
