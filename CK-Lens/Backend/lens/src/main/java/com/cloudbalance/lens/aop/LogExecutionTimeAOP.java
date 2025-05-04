package com.cloudbalance.lens.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
public class LogExecutionTimeAOP {
    @Around("execution(* com.cloudbalance.lens.controller..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.auth..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.onboarding..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.usermanagement..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.awsservices..*(..)) " +
            "|| execution(* com.cloudbalance.lens.repository..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Total execution time taken by {} is {} ms",joinPoint.getSignature().toShortString(), (endTime - startTime));
        return result;
    }
}
