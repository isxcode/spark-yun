package com.isxcode.spark.backend.api.base.exceptions;

public class IsxAppException extends AbstractIsxAppException {

    public IsxAppException(AbstractIsxAppExceptionEnum abstractSparkYunExceptionEnum) {
        super(abstractSparkYunExceptionEnum);
    }

    public IsxAppException(String code, String msg, String err) {
        super(code, msg, err);
    }

    public IsxAppException(String code, String msg) {
        super(code, msg);
    }

    public IsxAppException(String msg) {
        super(msg);
    }
}
