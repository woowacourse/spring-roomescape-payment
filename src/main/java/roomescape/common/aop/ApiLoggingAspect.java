package roomescape.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
@Component
public class ApiLoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public void controller() {
    }

    @Around("controller()")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();

        log.info("[요청] {} {}, controller: {}, args: {}, ip: {}",
            request.getMethod(),
            request.getRequestURI(),
            joinPoint.getSignature().getDeclaringTypeName(),
            args,
            request.getRemoteAddr());

        try {
            Object response = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.info("[응답] {} {}, response: {}, executionTime: {}",
                request.getMethod(),
                request.getRequestURI(),
                response,
                executionTime);

            return response;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.error("[예외] {} {}, executionTime: {}ms, message: {}",
                request.getMethod(),
                request.getRequestURI(),
                executionTime,
                e.getMessage());

            throw e;
        }
    }
}
