package com.isxcode.star.backend.api.base.exceptions;

public class IsxErrorException extends AbstractIsxAppException {

    public IsxErrorException(AbstractIsxAppExceptionEnum abstractSparkYunExceptionEnum) {
        super(abstractSparkYunExceptionEnum);
    }

    public IsxErrorException(String code, String msg, String err) {
        super(code, msg, err);
    }

    public IsxErrorException(String code, String msg) {
        super(code, msg);
    }

    public IsxErrorException(String msg) {
        super(msg);
    }
}
