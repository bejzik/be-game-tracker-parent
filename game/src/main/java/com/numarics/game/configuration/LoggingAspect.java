package com.numarics.game.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

  @Around("execution(* com.numarics.game.service..*.*(..))")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    Object[] args = joinPoint.getArgs();

    LOGGER.info("Entering {}.{}() with arguments: {}", className, methodName, args);

    try {
      // Proceed with the original method invocation
      Object result = joinPoint.proceed();

      LOGGER.info("Exiting {}.{}() with result: {}", className, methodName, result);

      return result;
    } catch (Exception ex) {
      LOGGER.error("Exception in {}.{}(): {}", className, methodName, ex.getMessage());
      throw ex; // Re-throw the exception after logging
    }
  }
}
