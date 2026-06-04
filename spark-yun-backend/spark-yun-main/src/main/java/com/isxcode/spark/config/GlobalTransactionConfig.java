package com.isxcode.spark.config;

import java.util.List;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
public class GlobalTransactionConfig {

    private static final String SERVICE_POINTCUT = "execution(public * com.isxcode.spark..service..*(..))"
        + " && !@within(jakarta.transaction.Transactional)"
        + " && !@annotation(jakarta.transaction.Transactional)"
        + " && !@within(org.springframework.transaction.annotation.Transactional)"
        + " && !@annotation(org.springframework.transaction.annotation.Transactional)";

    @Bean
    public TransactionInterceptor serviceTransactionInterceptor(PlatformTransactionManager transactionManager) {

        RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();
        transactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionAttribute.setRollbackRules(List.of(new RollbackRuleAttribute(Exception.class)));

        NameMatchTransactionAttributeSource transactionAttributeSource = new NameMatchTransactionAttributeSource();
        transactionAttributeSource.addTransactionalMethod("*", transactionAttribute);

        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionManager(transactionManager);
        transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
        return transactionInterceptor;
    }

    @Bean
    public Advisor serviceTransactionAdvisor(TransactionInterceptor serviceTransactionInterceptor) {

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(SERVICE_POINTCUT);

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, serviceTransactionInterceptor);
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return advisor;
    }
}
