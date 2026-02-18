package org.example.jle.productapi.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class ExecutionLoggingTime {

    @Around("execution(* org.example.jle.productapi..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object proceed = joinPoint.proceed();
        stopWatch.stop();

        log.info("Method: {} | Execution time: {} ms",
                joinPoint.getSignature().toShortString(),
                stopWatch.getTotalTimeMillis());

        return proceed;
    }
}
