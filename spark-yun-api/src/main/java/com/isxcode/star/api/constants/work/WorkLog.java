package com.isxcode.star.api.constants.work;

import java.time.LocalDateTime;

public interface WorkLog {

  String SUCCESS_INFO = LocalDateTime.now() + " INFO  : ";

  String ERROR_INFO = LocalDateTime.now() + " ERROR : ";
}
