package com.isxcode.star.config;

import static com.isxcode.star.security.main.WebSecurityConfig.JPA_TENANT_MODE;
import static com.isxcode.star.security.main.WebSecurityConfig.TENANT_ID;

import org.apache.logging.log4j.util.Strings;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class JpaTenantInterceptor implements StatementInspector {

  @Override
  public String inspect(String sql) {

    // 租户id不存在，则使用普通查询
    if (Strings.isEmpty(TENANT_ID.get())) {
      return sql.replace("${TENANT_FILTER}", "");
    }

    // 当时租户模式开启是，使用租户查询
    if (JPA_TENANT_MODE.get() == null || JPA_TENANT_MODE.get()) {
      return sql.replace("${TENANT_FILTER}", " and tenant_id = '" + TENANT_ID.get() + "' ");
    } else {
      return sql.replace("${TENANT_FILTER}", "");
    }
  }
}
