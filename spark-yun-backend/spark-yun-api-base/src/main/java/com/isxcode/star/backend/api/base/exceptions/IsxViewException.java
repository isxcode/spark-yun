package com.isxcode.star.backend.api.base.exceptions;

public class IsxViewException extends AbstractIsxAppException {

    public IsxViewException(AbstractIsxAppExceptionEnum abstractSparkYunExceptionEnum) {
        super(abstractSparkYunExceptionEnum);
    }

    public IsxViewException(String code, String msg, String err) {
        super(code, msg, err);
    }

    public IsxViewException(String code, String msg) {
        super(code, msg);
    }

    public IsxViewException(String msg) {
        super(msg);
    }
}
