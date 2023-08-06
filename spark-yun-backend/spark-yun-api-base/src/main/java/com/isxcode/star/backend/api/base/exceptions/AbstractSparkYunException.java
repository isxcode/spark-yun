package com.isxcode.star.backend.api.base.exceptions;

import com.isxcode.star.backend.api.base.exceptions.menus.AbstractSparkYunExceptionEnum;
import lombok.Getter;

/** 异常抽象类. */
public abstract class AbstractSparkYunException extends RuntimeException {

  @Getter private final String code;

  @Getter private final String msg;

  @Getter private final String err;

  public AbstractSparkYunException(AbstractSparkYunExceptionEnum abstractSparkYunExceptionEnum) {

    this.code = abstractSparkYunExceptionEnum.getCode();
    this.msg = abstractSparkYunExceptionEnum.getMsg();
    this.err = null;
  }

  public AbstractSparkYunException(String code, String msg, String err) {

    this.code = code;
    this.msg = msg;
    this.err = err;
  }

  public AbstractSparkYunException(String code, String msg) {

    this.code = code;
    this.msg = msg;
    this.err = null;
  }

  public AbstractSparkYunException(String msg) {

    this.code = null;
    this.msg = msg;
    this.err = null;
  }
}
