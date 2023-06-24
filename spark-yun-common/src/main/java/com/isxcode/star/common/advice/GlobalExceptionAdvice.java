package com.isxcode.star.common.advice;

import com.isxcode.star.api.exceptions.AbstractSparkYunException;
import com.isxcode.star.api.exceptions.SuccessResponseException;
import com.isxcode.star.api.pojos.base.BaseResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
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
        ? String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
        : abstractSparkYunException.getCode());
    errorResponse.setErr(
      abstractSparkYunException.getErr() == null ? null : abstractSparkYunException.getErr());

    if ("401".equals(abstractSparkYunException.getCode())) {
      return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    if ("403".equals(abstractSparkYunException.getCode())) {
      return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    return new ResponseEntity<>(errorResponse, HttpStatus.OK);
  }

  @ExceptionHandler(SuccessResponseException.class)
  public ResponseEntity<BaseResponse<Object>> successException(
    SuccessResponseException successException) {

    return new ResponseEntity<>(successException.getBaseResponse(), HttpStatus.OK);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<BaseResponse<Object>> accessDeniedException(
    AccessDeniedException accessDeniedException) {

    BaseResponse baseResponse = new BaseResponse();
    baseResponse.setCode("401");
    baseResponse.setMsg("当前用户没有权限");
    baseResponse.setErr(accessDeniedException.getMessage());

    return new ResponseEntity<>(baseResponse, HttpStatus.OK);
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<BaseResponse<Object>> emptyResultDataAccessException(
    EmptyResultDataAccessException emptyResultDataAccessException) {

    BaseResponse baseResponse = new BaseResponse();
    baseResponse.setCode("55500");
    baseResponse.setMsg("请稍后再试");
    baseResponse.setErr(emptyResultDataAccessException.getMessage());

    return new ResponseEntity<>(baseResponse, HttpStatus.OK);
  }

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<BaseResponse<Object>> objectOptimisticLockingFailureException(
    ObjectOptimisticLockingFailureException objectOptimisticLockingFailureException) {

    BaseResponse baseResponse = new BaseResponse();
    baseResponse.setCode("55500");
    baseResponse.setMsg("请稍后再试");
    baseResponse.setErr(objectOptimisticLockingFailureException.getMessage());

    return new ResponseEntity<>(baseResponse, HttpStatus.OK);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<?>> allException(Exception exception) {

    BaseResponse<?> baseResponse = new BaseResponse<>();
    baseResponse.setCode(String.valueOf(HttpStatus.OK.value()));
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
        String.valueOf(HttpStatus.BAD_REQUEST.value()),
        objectError.getDefaultMessage(),
        "请求参数不合法"),
      HttpStatus.OK);
  }
}
