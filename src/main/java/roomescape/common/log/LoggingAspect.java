package roomescape.common.log;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import roomescape.common.exception.AlreadyInUseException;
import roomescape.common.exception.AuthenticationException;
import roomescape.common.exception.EntityNotFoundException;
import roomescape.common.exception.LoginFailException;
import roomescape.common.exception.PaymentBadRequestException;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* roomescape..controller..*(..))")
    public void logControllerEntry(final JoinPoint joinPoint) {
        log.info("[정보] API 요청: {}.{}({})",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(pointcut = "within(roomescape..*)", throwing = "throwable")
    public void logException(final JoinPoint joinPoint, final Throwable throwable) {
        if (isExpectedException(throwable)) {
            log.info("[예외] 예상한 예외 발생: {}", throwable.getMessage());
        } else {
            log.error("[오류] 예상하지 못한 예외 발생: {}.{}() - {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    throwable.getMessage(),
                    throwable
            );
        }
    }

    private boolean isExpectedException(final Throwable throwable) {
        return throwable instanceof IllegalArgumentException
               || throwable instanceof PaymentBadRequestException
               || throwable instanceof EntityNotFoundException
               || throwable instanceof AlreadyInUseException
               || throwable instanceof LoginFailException
               || throwable instanceof AuthenticationException;
    }

    @Around("execution(* roomescape.payment.service.PaymentService.confirm(..))")
    public Object logPaymentProcessingTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long callTime = end - start;
            int twoSeconds = 2_000;
            if (callTime > twoSeconds) {
                log.warn("[경고] 결제 확인 응답 지연: {}ms", callTime);
            } else {
                log.info("[정보] 결제 확인 소요 시간: {}ms", callTime);
            }
        }
    }
}
