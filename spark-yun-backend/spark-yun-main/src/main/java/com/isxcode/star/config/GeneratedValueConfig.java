package com.isxcode.star.config;

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/** id生成器. */
public class GeneratedValueConfig implements IdentifierGenerator {

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {

    return "sy_" + UUID.randomUUID().toString().replace("-", "");
  }
}
