package roomescape.exception.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReservationLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ReservationLoggingAspect.class);

    @Around("@annotation(roomescape.exception.aspect.ReservationLogging)")
    public Object logPaymentExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();

        try {
            logger.info("{}.{} 시작", className, methodName);

            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("{} 완료 - 소요시간: {}ms", methodName, executionTime);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("{}.{} 실패 - 오류: {}, 소요시간: {}ms", className, methodName, e.getMessage(), executionTime);
            throw e;
        }
    }
}
