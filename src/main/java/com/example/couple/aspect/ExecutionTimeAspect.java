package com.example.couple.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ExecutionTimeAspect {

  @Around("@annotation(com.example.couple.annotation.LogExecutionTime)")
  public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    try {
      return proceedingJoinPoint.proceed();
    } finally {
      long executionTime = System.currentTimeMillis() - startTime;
      log.info(
          "{} metodu {} ms sürede çalıştı.", proceedingJoinPoint.getSignature(), executionTime);
    }
  }
}
