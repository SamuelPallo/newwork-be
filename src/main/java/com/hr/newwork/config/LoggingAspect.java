package com.hr.newwork.config;

import com.hr.newwork.util.SensitiveDataSanitizer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut for all controller methods
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    // Pointcut for all service methods
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    // Pointcut for all mapper methods (assuming mappers are in util.mappers package)
    @Pointcut("within(com.hr.newwork.util.mappers..*)")
    public void mapperMethods() {}

    // Pointcut for all repository methods (annotated with @Repository or in repositories package)
    @Pointcut("within(@org.springframework.stereotype.Repository *) || within(com.hr.newwork.repositories..*)")
    public void repositoryMethods() {}

    // Pointcut for RestTemplate bean methods (calls to exchange, getForObject, etc.)
    @Pointcut("execution(* org.springframework.web.client.RestTemplate.*(..))")
    public void restTemplateMethods() {}

    // Combined pointcut for controller and service
    @Pointcut("restControllerMethods() || serviceMethods() || repositoryMethods() || mapperMethods() || restTemplateMethods()")
    public void fullApplicationFlow() {}

    @Around("restControllerMethods()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("[CONTROLLER] START: {}.{} args={}", className, methodName, SensitiveDataSanitizer.sanitizeArgs(args));
        try {
            Object result = joinPoint.proceed();
            logger.info("[CONTROLLER] END: {}.{} result={}", className, methodName, SensitiveDataSanitizer.sanitizeObject(result));
            return result;
        } catch (Throwable ex) {
            logger.error("[CONTROLLER] EXCEPTION: {}.{}: {}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Around("serviceMethods()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("[SERVICE] CALL: {}.{} args={}", className, methodName, SensitiveDataSanitizer.sanitizeArgs(args));
        try {
            Object result = joinPoint.proceed();
            logger.info("[SERVICE] RETURN: {}.{} result={}", className, methodName, SensitiveDataSanitizer.sanitizeObject(result));
            return result;
        } catch (Throwable ex) {
            logger.error("[SERVICE] EXCEPTION: {}.{}: {}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Around("repositoryMethods()")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("[REPOSITORY] CALL: {}.{} args={}", className, methodName, SensitiveDataSanitizer.sanitizeArgs(args));
        try {
            Object result = joinPoint.proceed();
            logger.info("[REPOSITORY] RETURN: {}.{} result={}", className, methodName, SensitiveDataSanitizer.sanitizeObject(result));
            return result;
        } catch (Throwable ex) {
            logger.error("[REPOSITORY] EXCEPTION: {}.{}: {}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Around("mapperMethods()")
    public Object logMapper(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("[MAPPER] CALL: {}.{} args={}", className, methodName, SensitiveDataSanitizer.sanitizeArgs(args));
        try {
            Object result = joinPoint.proceed();
            logger.info("[MAPPER] RETURN: {}.{} result={}", className, methodName, SensitiveDataSanitizer.sanitizeObject(result));
            return result;
        } catch (Throwable ex) {
            logger.error("[MAPPER] EXCEPTION: {}.{}: {}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Around("restTemplateMethods()")
    public Object logRestTemplate(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("[RESTTEMPLATE] CALL: {} args={}", methodName, SensitiveDataSanitizer.sanitizeArgs(args));
        try {
            Object result = joinPoint.proceed();
            logger.info("[RESTTEMPLATE] RETURN: {} result={}", methodName, SensitiveDataSanitizer.sanitizeObject(result));
            return result;
        } catch (Throwable ex) {
            logger.error("[RESTTEMPLATE] EXCEPTION: {}: {}", methodName, ex.getMessage(), ex);
            throw ex;
        }
    }
}
