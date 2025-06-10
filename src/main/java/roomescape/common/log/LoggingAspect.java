package roomescape.common.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Around("execution(* roomescape..*Controller.*(..)) || " +
            "execution(* roomescape..*Service.*(..)) ")
    public Object traceLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getName();
        log.info("[Start] {}", method);
        try {
            Object result = joinPoint.proceed();
            log.info("[end] {}", method);
            return result;
        } catch (Exception e) {
            log.error("[ERROR] {} - {}", method, e.getMessage());
            throw e;
        }
    }
}
