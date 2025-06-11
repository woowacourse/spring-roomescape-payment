package roomescape.log;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * roomescape.controller.api..*.*(..))")
    private void publicMethodsFromApiPackage() {
    }

    @Before(value = "publicMethodsFromApiPackage()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        log.debug("[REQUEST] {}() - {}", methodName, Arrays.toString(args));
    }

    @AfterReturning(value = "publicMethodsFromApiPackage()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.debug("[RESPONSE] {}() - {}", methodName, result);
    }
}
