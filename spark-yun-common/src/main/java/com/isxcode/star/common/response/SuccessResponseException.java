package com.isxcode.star.common.response;

import lombok.Getter;
import lombok.Setter;

public class SuccessResponseException extends RuntimeException {

  @Setter @Getter private BaseResponse<Object> baseResponse;

  public SuccessResponseException(BaseResponse<Object> baseResponse) {

    this.baseResponse = baseResponse;
  }
}
