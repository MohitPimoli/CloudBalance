package com.cloudbalance.lens.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LogParamsAOP {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    @Before("execution(* com.cloudbalance.lens.controller..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.auth..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.usermanagement..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.awsservices..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.onboarding..*(..))")
    public void logMethodParams(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        System.out.println(ANSI_BLUE + "Executing: " + methodName + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "Parameters: " + Arrays.toString(args) + ANSI_RESET);
    }
}