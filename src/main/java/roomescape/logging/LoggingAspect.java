package roomescape.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* roomescape.service..*(..))")
    public void serviceMethod() {
    }

    @Before("serviceMethod()")
    public void logBefore(JoinPoint joinPoint) {
        Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        log.debug("진입 : {}.{}({})",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethod()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        log.debug("정상 종료 : {}.{} - 반환값: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(pointcut = "serviceMethod()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        log.debug("예외 발생 : {}.{} - 반환값: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getMessage(), ex);
    }
}
