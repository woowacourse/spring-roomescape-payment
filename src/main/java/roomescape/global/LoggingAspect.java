package roomescape.global;

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
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(roomescape.controller.api..*)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUri = requestURI + (queryString == null ? "" : "?" + queryString);
        Object[] args = joinPoint.getArgs();

        log.info("""
                [Request]
                Method: {},
                Uri: {},
                Args: {}
                """, method, fullUri, args);

        try {
            Object result = joinPoint.proceed();

            long elapsed = System.currentTimeMillis() - start;
            log.info("""
                    [Response]
                    Method: {},
                    Time: {} ms
                    Result: {}
                    """, method, elapsed, result);
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("""
                    [Error]
                    Method: {},
                    Time: {} ms,
                    Message: {}
                    """, method, elapsed, e.getMessage());
            throw e;
        }
    }
}
