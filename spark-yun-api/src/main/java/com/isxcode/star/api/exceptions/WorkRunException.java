package com.isxcode.star.api.exceptions;

import com.isxcode.star.api.exceptions.menus.AbstractSparkYunExceptionEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkRunException extends AbstractSparkYunException {

  public WorkRunException(AbstractSparkYunExceptionEnum abstractSparkYunExceptionEnum) {
    super(abstractSparkYunExceptionEnum);
  }

  public WorkRunException(String code, String msg, String err) {
    super(code, msg, err);
  }

  public WorkRunException(String code, String msg) {
    super(code, msg);
  }

  public WorkRunException(String msg) {
    super(msg);
  }
}
