package com.isxcode.star.backend.config;

import org.apache.logging.log4j.util.Strings;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import static com.isxcode.star.backend.config.WebSecurityConfig.JPA_TENANT_MODE;
import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;

public class JpaTenantInterceptor implements StatementInspector {

  @Override
  public String inspect(String sql) {

    // 租户id不存在，则使用普通查询
    if (Strings.isEmpty(TENANT_ID.get())) {
      return sql;
    }

    // 当时租户模式开启是，使用租户查询
    if (JPA_TENANT_MODE.get() == null || JPA_TENANT_MODE.get()) {
      return sql + " and ( tenant_id = '" + TENANT_ID.get() + "' ) ";
    } else {
      return sql;
    }
  }
}
