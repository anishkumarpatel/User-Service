package com.unisys.udb.user.config;

import com.unisys.udb.utility.auditing.dto.CustomerActionAuditHolder;
import com.unisys.udb.utility.auditing.producer.UdbCustomerActionAuditKafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

@Configuration
@EnableCaching
public class CustomerAuditConfig {

    @Bean
    public UdbCustomerActionAuditKafkaProducer udbCustomerActionAuditKafkaProducer() {
        return new UdbCustomerActionAuditKafkaProducer();
    }

    @Bean
    @Qualifier("customerAuditLogAdvisor")
    public Advisor customerAuditLogAdvisor(UdbCustomerActionAuditKafkaProducer udbCustomerAuditKafkaProducer) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.unisys.udb.utility.auditing.annotation.CustomerAuditing)");
        return new DefaultPointcutAdvisor(pointcut, udbCustomerAuditKafkaProducer);
    }

    @Bean
    public CustomerActionAuditHolder customerActionAuditHolder() {
        return new CustomerActionAuditHolder();
    }
}