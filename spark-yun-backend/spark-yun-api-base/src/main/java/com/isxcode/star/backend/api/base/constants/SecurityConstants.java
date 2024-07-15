package com.isxcode.star.backend.api.base.constants;

/** 安全相关的静态变量. */
public interface SecurityConstants {

    /** token的请求头名称. */
    String HEADER_AUTHORIZATION = "Authorization";

    /** tenantId的请求头名称. */
    String HEADER_TENANT_ID = "Tenant";

    /** token为null异常抛出. */
    String TOKEN_IS_NULL_PATH = "/exception/tokenIsNull";

    /** token不正确异常抛出. */
    String TOKEN_IS_INVALID_PATH = "/exception/tokenIsInvalid";

    /** token没有权限异常抛出. */
    String AUTH_ERROR_PATH = "/exception/authError";
}
