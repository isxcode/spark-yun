package com.isxcode.star.backend.api.base.exceptions;

import lombok.Getter;

/** 异常抽象类. */
@Getter
public abstract class AbstractIsxAppException extends RuntimeException {

    /**
     * 异常编码.
     */
    private final String code;

    /**
     * 异常的中文信息.
     */
    private final String msg;

    /**
     * 异常的英文信息.
     */
    private final String err;

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
