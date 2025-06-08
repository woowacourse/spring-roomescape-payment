package roomescape.common.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
public class LogAspect {

    @Pointcut("execution(* roomescape.reservation.ui..*Controller.*(..))")
    public void reservationControllers() {
    }

    @Pointcut("execution(* roomescape.reservation.application..*Service.*(..))")
    public void reservationServices() {
    }

    @Around("reservationControllers()")
    public Object logReservationApi(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String controller = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String action = joinPoint.getSignature().getName();
        Map<String, String> params = getParams(request);

        long start = System.currentTimeMillis();
        try {
            log.info(">> [{} {}] {}.{} | params: {}", method, uri, controller, action, params);
            Object result = joinPoint.proceed();
            return result;
        } finally {
            long end = System.currentTimeMillis();
            log.info("<< [{} {}] {}.{} | duration: {}ms", method, uri, controller, action, end - start);
        }
    }

    @Around("reservationServices()")
    public Object logReservationService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        try {
            log.info(">> [SERVICE] {}.{} called", className, methodName);
            Object result = joinPoint.proceed();
            log.info("<< [SERVICE] {}.{} completed", className, methodName);
            return result;
        } catch (Throwable e) {
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            log.info("-- [SERVICE] {}.{} took {}ms", className, methodName, end - start);
        }
    }

    private Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String value = request.getParameter(param);
            paramMap.put(param.replaceAll("\\.", "-"), value);
        }

        return paramMap;
    }
}
