package com.isxcode.star.backend.api.base.exceptions;

import lombok.Getter;

/** 异常抽象类. */
public abstract class AbstractIsxAppException extends RuntimeException {

  @Getter private final String code;

  @Getter private final String msg;

  @Getter private final String err;

  public AbstractIsxAppException(AbstractIsxAppExceptionEnum abstractSparkYunExceptionEnum) {

    this.code = abstractSparkYunExceptionEnum.getCode();
    this.msg = abstractSparkYunExceptionEnum.getMsg();
    this.err = null;
  }

  public AbstractIsxAppException(String code, String msg, String err) {

    this.code = code;
    this.msg = msg;
    this.err = err;
  }

  public AbstractIsxAppException(String code, String msg) {

    this.code = code;
    this.msg = msg;
    this.err = null;
  }

  public AbstractIsxAppException(String msg) {

    this.code = null;
    this.msg = msg;
    this.err = null;
  }
}
