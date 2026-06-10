package com.isxcode.spark.common.security;

import java.io.Serializable;

public record RefreshUserToken(String userId,String tenantId,String tokenType)implements Serializable{}
