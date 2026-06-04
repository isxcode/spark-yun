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

        Session session = entityManager.unwrap(Session.class);
        List<String> tenantIds = JpaTenantContext.getVisibleTenantIds();
        if (tenantIds.isEmpty()) {
            session.disableFilter(TENANT_FILTER);
        } else {
            session.enableFilter(TENANT_FILTER).setParameterList(TENANT_IDS_PARAM, tenantIds);
        }

        return joinPoint.proceed();
    }
}
