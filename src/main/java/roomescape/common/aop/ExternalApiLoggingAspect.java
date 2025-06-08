package roomescape.common.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class ExternalApiLoggingAspect {

    @Pointcut("execution(* roomescape.payment.service.client..*.*(..))")
    public void externalApiClient() {
    }

    @Around("externalApiClient()")
    public Object logExternalApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        log.info("[외부 API 요청] {}.{}, controller: {}, args: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            joinPoint.getSignature().getDeclaringTypeName(),
            Arrays.toString(joinPoint.getArgs()));

        try {
            Object response = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.info("[외부 API 응답] {}.{}, response: {}, executionTime: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                response,
                executionTime);

            return response;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.error("[외부 API 예외] {} {}, executionTime: {}ms, message: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                executionTime,
                e.getMessage());

            throw e;
        }
    }
}
