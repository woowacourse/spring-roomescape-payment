package roomescape.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(roomescape.common.logging.LogExecution) || within(@roomescape.common.logging.LogExecution *)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info(">> 진입: {}", methodName);
        try {
            Object result = joinPoint.proceed();
            log.info("<< 정상 종료: {}", methodName);
            return result;
        } catch (Throwable e) {
            log.error("!! 예외 발생: {}", methodName, e);
            throw e;
        }
    }
}
