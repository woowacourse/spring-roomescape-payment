package roomescape.aspect;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(* roomescape.core.controller.*Controller.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* roomescape.core.service.PaymentService.*(..))")
    public void paymentService() {
    }

    @Pointcut("execution(* roomescape.core.controller.*ExceptionHandler.*(..))")
    public void exceptionHandler() {
    }

    @Around("controller() || paymentService()")
    public Object loggingController(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        logger.info("method = {}", method.getName());

        final Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            logger.info("parameter type = {}", arg.getClass().getSimpleName());
            logger.info("parameter value = {}", arg);
        }

        final Object result = joinPoint.proceed();
        if (result instanceof String) {
            logger.info("return view name = {}", result);
        }
        if (result instanceof ResponseEntity<?> response) {
            logger.info("return type = {}", response.getClass().getSimpleName());
            logger.info("return value = {}", response.getBody());
        }
        return result;
    }

    @Around("exceptionHandler()")
    public Object loggingExceptionHandler(final ProceedingJoinPoint joinPoint) throws Throwable {
        for (final Object argument : joinPoint.getArgs()) {
            final RuntimeException exception = (RuntimeException) argument;
            logger.error("exception occurred = {}", argument.getClass().getSimpleName());
            logger.error("exception message = {}", exception.getMessage(), exception);
        }
        return joinPoint.proceed();
    }
}
