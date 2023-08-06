package com.isxcode.star.backend.api.base.constants;

public interface SecurityConstants {

  String HEADER_AUTHORIZATION = "Authorization";

  String HEADER_TENANT_ID = "Tenant";

  String TOKEN_IS_NULL_PATH = "/exception/tokenIsNull";

  String TOKEN_IS_INVALID_PATH = "/exception/tokenIsInvalid";

  String LICENSE_IS_ERROR = "/exception/licenseError";

  String AUTH_ERROR_PATH = "/exception/authError";
}
