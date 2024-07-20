package com.isxcode.star.config;

import java.io.Serializable;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/** id生成器. */
public class GeneratedValueConfig implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        Snowflake snowflake = IdUtil.getSnowflake();
        return "sy_" + snowflake.nextIdStr();
    }
}
