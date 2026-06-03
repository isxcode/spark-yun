package com.isxcode.spark.config;

import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;

public class JpaTableUpperCaseStrategy extends CamelCaseToUnderscoresNamingStrategy {

    @Override
    protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {

        return false;
    }
}
