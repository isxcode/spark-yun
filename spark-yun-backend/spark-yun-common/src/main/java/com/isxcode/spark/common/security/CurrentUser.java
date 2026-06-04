package com.isxcode.spark.common.security;

import java.io.Serializable;

public record CurrentUser(String userId, String tenantId) implements Serializable {
}
