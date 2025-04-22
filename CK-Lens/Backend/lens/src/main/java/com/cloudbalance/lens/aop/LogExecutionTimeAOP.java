package com.cloudbalance.lens.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class LogExecutionTimeAOP {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";

    @Around("execution(* com.cloudbalance.lens.controller..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.auth..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.onboarding..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.usermanagement..*(..)) " +
            "|| execution(* com.cloudbalance.lens.service.awsservices..*(..)) " +
            "|| execution(* com.cloudbalance.lens.repository..*(..))")
    public Object LogExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        System.out.println(GREEN + "Execution started for: " + joinPoint.getSignature() + RESET);
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println(BLUE + "Execution ended for: " + joinPoint.getSignature() + RESET);
        System.out.println(RED + "Total execution time: " + (endTime - startTime) + " ms" + RESET);
        return result;
    }
}
