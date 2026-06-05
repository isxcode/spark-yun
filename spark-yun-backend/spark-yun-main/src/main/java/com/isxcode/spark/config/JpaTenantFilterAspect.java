package com.isxcode.spark.config;

import static com.isxcode.spark.common.jpa.JpaTenantContext.TENANT_FILTER;
import static com.isxcode.spark.common.jpa.JpaTenantContext.TENANT_IDS_PARAM;

import com.isxcode.spark.common.jpa.JpaTenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JpaTenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Around("execution(* com.isxcode.spark..repository..*(..))")
    public Object enableTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {

        // 使用aop，过滤所有的jpa请求
        Session session = entityManager.unwrap(Session.class);

        // 通过 noTenant() 和 allData()  函数，配置getVisibleTenantIds的参数
        // noTenant() 插入当前租户和zhiqingyun
        // allData()  不走tenant过滤器
        // 默认只从 ContextHolder中获 tenantId
        List<String> tenantIds = JpaTenantContext.getVisibleTenantIds();

        // 是否开启租户
        if (tenantIds.isEmpty()) {
            session.disableFilter(TENANT_FILTER);
        } else {
            session.enableFilter(TENANT_FILTER)
                .setParameterList(TENANT_IDS_PARAM, tenantIds);
        }

        return joinPoint.proceed();
    }
}
