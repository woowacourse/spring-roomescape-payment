package roomescape.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

    @Around("@annotation(roomescape.aop.ServiceLogging)")
    public Object logForReservation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("📌[AOP] 메서드 호출: {} 인자: {}", methodName, args);

        try {
            Object result = joinPoint.proceed();
            log.info("✅[AOP] 실행 완료: {} 결과: {}", methodName, result);
            return result;
        } catch (Exception e) {
            log.error("❌[AOP] 예외 발생: {}", methodName, e);
            throw e;
        }
    }
}
