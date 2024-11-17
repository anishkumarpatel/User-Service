package com.unisys.udb.user.config;

import com.unisys.udb.utility.auditing.dto.AuditDigitalCustomerHolder;
import com.unisys.udb.utility.auditing.dto.BankAuditHolder;
import com.unisys.udb.utility.auditing.producer.UdbAuditKafkaProducer;
import com.unisys.udb.utility.auditing.producer.UdbBankAuditKafkaProducer;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableCaching
public class AuditConfig {

    @Bean
    public UdbAuditKafkaProducer udbAuditKafkaProducer() {
        return new UdbAuditKafkaProducer();
    }


    @Bean
    public Advisor auditLogAdvisor(UdbAuditKafkaProducer udbAuditKafkaProducer) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.unisys.udb.utility.auditing.annotation.Auditing)");
        return new DefaultPointcutAdvisor(pointcut, udbAuditKafkaProducer);
    }

    @Bean
    public AuditDigitalCustomerHolder auditDigitalCustomerHolder() {
        return new AuditDigitalCustomerHolder();
    }

    @Bean
    public UdbBankAuditKafkaProducer udbBankAuditKafkaProducer() {
        return new UdbBankAuditKafkaProducer();
    }
    @Bean
    public Advisor bankAuditLogAdvisor(UdbBankAuditKafkaProducer udbBankAuditKafkaProducer) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.unisys.udb.utility.auditing.annotation.BankAudit)");
        return new DefaultPointcutAdvisor(pointcut, udbBankAuditKafkaProducer);
    }
    @Bean
    public BankAuditHolder bankAuditHolder() {
        return new BankAuditHolder();
    }
}