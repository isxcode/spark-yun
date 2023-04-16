package com.isxcode.star.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
