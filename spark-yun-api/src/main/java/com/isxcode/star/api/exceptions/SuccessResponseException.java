package com.isxcode.star.api.exceptions;

import com.isxcode.star.api.pojos.base.BaseResponse;
import lombok.Getter;
import lombok.Setter;

public class SuccessResponseException extends RuntimeException {

  @Setter @Getter private BaseResponse<Object> baseResponse;

  public SuccessResponseException(BaseResponse<Object> baseResponse) {

    this.baseResponse = baseResponse;
  }
}
