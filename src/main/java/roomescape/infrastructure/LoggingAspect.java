package roomescape.infrastructure;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import roomescape.infrastructure.exception.AuthenticationException;
import roomescape.infrastructure.exception.AuthorizationException;
import roomescape.infrastructure.exception.DataNotFoundException;
import roomescape.infrastructure.exception.DomainException;
import roomescape.infrastructure.exception.DomainRuleException;
import roomescape.infrastructure.exception.DuplicateException;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("within(roomescape.controller..*)")
    public void logControllerEntry(final JoinPoint joinPoint) {
        logger.info("[INFO] API 호출: {}.{}({})",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @Before("within(roomescape.domain..service..*)")
    public void logServiceEntry(final JoinPoint joinPoint) {
        logger.info("[INFO] Service 호출: {}.{}({})",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "within(roomescape.domain..service..*)", returning = "result")
    public void logServiceReturn(final JoinPoint joinPoint, final Object result) {
        logger.info("[INFO] Service 반환: {}.{}() -> {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(pointcut = "within(roomescape..*)", throwing = "ex")
    public void logException(final JoinPoint joinPoint, final Throwable ex) {
        if (ex instanceof DataNotFoundException || ex instanceof DomainException
                || ex instanceof DuplicateException || ex instanceof DomainRuleException
                || ex instanceof AuthenticationException || ex instanceof AuthorizationException) {
            logger.warn("[WARN] 예상 예외 발생: {}", ex.getMessage());
        } else {
            logger.error("[ERROR] 예상치 못한 예외 발생: {}.{}() - {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    ex.getMessage(), ex);
        }
    }

    @Around("execution(* roomescape.domain.payment.service.PaymentService.processPayment(..))")
    public Object logPaymentProcessingTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            final long end = System.currentTimeMillis();
            final long elapsed = end - start;
            if (elapsed > 5000) {
                logger.warn("[WARN] 결제 응답 지연: {}ms", elapsed);
            } else {
                logger.info("[INFO] 결제 프로세스 소요 시간: {}ms", elapsed);
            }
        }
    }
}
