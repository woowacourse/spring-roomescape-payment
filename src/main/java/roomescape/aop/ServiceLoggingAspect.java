package roomescape.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("@annotation(roomescape.aop.ServiceLogging)")
    public Object logForReservation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("📌[AOP] 메서드 호출: {} 인자: {}", methodName, args);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("✅[AOP] 실행 완료: {} 결과: {} ({}ms)", methodName, result, endTime - startTime);
            return result;
        } catch (Exception e) {
            log.error("❌[AOP] 예외 발생: {}", methodName, e);
            throw e;
        }
    }
}
