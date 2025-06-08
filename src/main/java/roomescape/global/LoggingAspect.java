package roomescape.global;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("within(roomescape.controller.api..*)")
    public void controllerMethods() {}

//    @Around("controllerMethods()")
//    public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
//
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        HttpServletRequest request = requestAttributes.getRequest();
//        String method = request.getMethod();
//        String requestURI = request.getRequestURI();
//        String queryString = request.getQueryString();
//        String fullUri = requestURI + (queryString == null ? "" : "?" + queryString);
//        Object[] args = joinPoint.getArgs();
//
//        log.info("""
//                [Request]
//                Method: {},
//                Uri: {},
//                Args: {}
//                """, method, fullUri, args);
//
//        try {
//            Object result = joinPoint.proceed();
//
//            long elapsed = System.currentTimeMillis() - start;
//            log.info("""
//                    [Response]
//                    Method: {},
//                    Time: {} ms
//                    Result: {}
//                    """, method, elapsed, result);
//            return result;
//        } catch (Exception e) {
//            long elapsed = System.currentTimeMillis() - start;
//            log.error("""
//                    [Error]
//                    Method: {},
//                    Time: {} ms,
//                    Message: {}
//                    """, method, elapsed, e.getMessage());
//            throw e;
//        }
//    }

    @Before("controllerMethods()")
    public void beforeLog(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
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
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void afterLog(JoinPoint joinPoint, Object result) {
        long elapsed = System.currentTimeMillis() - startTime.get();
        log.info("""
                [Response]
                Method: {},
                Time: {} ms
                Result: {}
                """, joinPoint.getSignature().getName(), elapsed, result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void afterThrowingLog(JoinPoint joinPoint, Throwable ex) {
        long elapsed = System.currentTimeMillis() - startTime.get();
        log.error("""
                [Error]
                Method: {},
                Time: {} ms,
                Message: {}
                """, joinPoint.getSignature().getName(), elapsed, ex.getMessage());
    }
}
