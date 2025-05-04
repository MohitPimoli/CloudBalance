package com.cloudbalance.lens.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogParamsAOP {

    @Before("execution(* com.cloudbalance.lens.controller..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.auth..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.usermanagement..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.awsservices..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.onboarding..*(..))")
    public void logMethodParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("Parameters for {} are {}",joinPoint.getSignature().toShortString(), Arrays.toString(args));
    }
}