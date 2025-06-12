package roomescape.global.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(* roomescape..*Controller.*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(* roomescape..*Service.*(..))")
    public void serviceMethods() {
    }

    private String getMethodSignature(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName() + "()";
    }


    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        log.info("[Controller - ENTER] {}, args={}", getMethodSignature(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturningController(JoinPoint joinPoint, Object result) {
        log.info("[Controller - RETURN] {} : {}", getMethodSignature(joinPoint), result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "e")
    public void logAfterThrowingController(JoinPoint joinPoint, Throwable e) {
        log.error("[Controller - EXCEPTION] {} threw {},message : {}", getMethodSignature(joinPoint),
                e.getClass().getSimpleName(), e.getMessage());
    }


    @Before("serviceMethods()")
    public void logBeforeService(JoinPoint joinPoint) {
        log.info("[Service - ENTER] {}, args={}", getMethodSignature(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturningService(JoinPoint joinPoint, Object result) {
        log.info("[Service - RETURN] {} : {}", getMethodSignature(joinPoint), result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "e")
    public void logAfterThrowingService(JoinPoint joinPoint, Throwable e) {
        log.error("[Service - EXCEPTION] {} threw {},message : {}", getMethodSignature(joinPoint),
                e.getClass().getSimpleName(), e.getMessage());
    }
}
