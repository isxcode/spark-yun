package com.isxcode.spark.backend.api.base.exceptions;

import com.isxcode.spark.backend.api.base.pojos.BaseResponse;
import lombok.Getter;
import lombok.Setter;

public class SuccessResponseException extends RuntimeException {

    @Setter
    @Getter
    private BaseResponse<Object> baseResponse;

    public SuccessResponseException(BaseResponse<Object> baseResponse) {

        this.baseResponse = baseResponse;
    }
}
