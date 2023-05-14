package com.isxcode.star.api.response;

import com.isxcode.star.api.constants.CodeConstants;
import com.isxcode.star.api.exception.AbstractSparkYunException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@ResponseBody
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AbstractSparkYunException.class)
  public ResponseEntity<BaseResponse<?>> customException(
      AbstractSparkYunException abstractSparkYunException) {

    BaseResponse<?> errorResponse = new BaseResponse<>();
    errorResponse.setMsg(abstractSparkYunException.getMsg());
    errorResponse.setCode(
        abstractSparkYunException.getCode() == null
            ? CodeConstants.ERROR_CODE
            : abstractSparkYunException.getCode());
    errorResponse.setErr(
        abstractSparkYunException.getErr() == null ? null : abstractSparkYunException.getErr());

    // token异常
//    if ("5555".equals(abstractSparkYunException.getCode())) {
//      return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
//    }

    return new ResponseEntity<>(errorResponse, HttpStatus.OK);
  }

  @ExceptionHandler(SuccessResponseException.class)
  public ResponseEntity<BaseResponse<Object>> successException(
      SuccessResponseException successException) {

    return new ResponseEntity<>(successException.getBaseResponse(), HttpStatus.OK);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<?>> allException(Exception exception) {

    BaseResponse<?> baseResponse = new BaseResponse<>();
    baseResponse.setCode(CodeConstants.ERROR_CODE);
    baseResponse.setMsg(
        exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
    exception.printStackTrace();
    return new ResponseEntity<>(baseResponse, HttpStatus.OK);
  }

  @Override
  @NonNull
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatus status,
      @NonNull WebRequest request) {
    ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
    return new ResponseEntity<>(
        new BaseResponse<>(
            CodeConstants.BAD_REQUEST_CODE, "请求参数不合法", objectError.getDefaultMessage()),
        HttpStatus.OK);
  }
}
