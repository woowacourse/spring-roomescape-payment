package roomescape.support.logging;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ControllerLogAop {

    @Pointcut("execution(* roomescape.presentation.controller.*.*(..))")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long start = System.currentTimeMillis();

        log.info("[HTTP REQUEST] {} {} - args: {}", request.getMethod(), request.getRequestURI(),
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();

            log.info("[HTTP RESPONSE] {} {} - took {}ms", request.getMethod(), request.getRequestURI(), (end - start));
            return result;

        } catch (Exception e) {
            long end = System.currentTimeMillis();

            log.info("[HTTP RESPONSE - ERROR] {} {} - took {}ms", request.getMethod(), request.getRequestURI(),
                    (end - start));

            throw e;
        }

    }
}
