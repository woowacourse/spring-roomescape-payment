package roomescape.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    private static final int WARNING_THRESHOLD = 1000;

    @Pointcut("@annotation(roomescape.logging.aspect.Loggable)")
    public void logAnnotation() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void exceptionHandlerMethods() {}

    @Around("logAnnotation()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        try {
            log.info("[START] {} args={}", methodName, args);
            Object object = joinPoint.proceed();
            log.info("[END] {} result={}", methodName, object);
            return object;
        } catch (Exception e) {
            log.warn("[EXCEPTION] {} | 예외 발생", joinPoint.getSignature(), e);
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            long duration = end - start;
            // 실행 시간 기준 임계치 체크
            if (duration > WARNING_THRESHOLD) {
                log.warn("[PERFORMANCE] {} | 실행 시간: {} ms (느림)", joinPoint.getSignature(), duration);
            } else {
                log.info("[PERFORMANCE] {} | 실행 시간: {} ms", joinPoint.getSignature(), duration);
            }
        }
    }

    @Around("exceptionHandlerMethods()")
    public Object logExceptionHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        try {
            log.info("[ExceptionHandler-START] {} args={}", methodName, args);
            Object result = joinPoint.proceed();
            log.info("[ExceptionHandler-END] {} result={}", methodName, result);
            return result;
        } catch (Exception e) {
            log.warn("[ExceptionHandler-EXCEPTION] {} | 예외 발생: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}
