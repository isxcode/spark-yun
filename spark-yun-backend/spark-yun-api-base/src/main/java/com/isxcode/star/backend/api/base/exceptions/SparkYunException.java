package com.isxcode.star.backend.api.base.exceptions;

import com.isxcode.star.backend.api.base.exceptions.menus.AbstractSparkYunExceptionEnum;

public class SparkYunException extends AbstractSparkYunException {

  public SparkYunException(AbstractSparkYunExceptionEnum abstractSparkYunExceptionEnum) {
    super(abstractSparkYunExceptionEnum);
  }

  public SparkYunException(String code, String msg, String err) {
    super(code, msg, err);
  }

  public SparkYunException(String code, String msg) {
    super(code, msg);
  }

  public SparkYunException(String msg) {
    super(msg);
  }
}
