package com.isxcode.star.api.work.exceptions;

import com.isxcode.star.backend.api.base.exceptions.AbstractIsxAppException;
import com.isxcode.star.backend.api.base.exceptions.AbstractIsxAppExceptionEnum;

public class WorkRunException extends AbstractIsxAppException {

  public WorkRunException(AbstractIsxAppExceptionEnum abstractSparkYunExceptionEnum) {
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
