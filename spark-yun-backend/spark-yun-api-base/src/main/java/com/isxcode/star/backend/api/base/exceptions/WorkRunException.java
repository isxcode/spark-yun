package com.isxcode.star.backend.api.base.exceptions;

import com.isxcode.star.backend.api.base.exceptions.menus.AbstractSparkYunExceptionEnum;

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
