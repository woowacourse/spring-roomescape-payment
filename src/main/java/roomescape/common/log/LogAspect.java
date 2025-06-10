package roomescape.common.log;

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

    @Pointcut("execution(* roomescape.reservation.ui..*Controller.*(..)) && !execution(* roomescape.reservation.ui.ReservationTimeController.*(..))")
    public void reservationController() {
    }

    @Pointcut("execution(* roomescape.reservation.application..*Service.*(..)) && !execution(* roomescape.reservation.application.ReservationTimeService.*(..))")
    public void reservationService() {
    }

    @Around("reservationController()")
    public Object logReservationController(final ProceedingJoinPoint joinPoint) throws Throwable {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        final HttpServletRequest request = attributes.getRequest();
        final String method = request.getMethod();
        final String uri = request.getRequestURI();
        final String controller = joinPoint.getSignature().getDeclaringType().getSimpleName();
        final String action = joinPoint.getSignature().getName();
        final Map<String, String> params = getParams(request);

        long start = System.currentTimeMillis();
        try {
            log.info("[START][CONTROLLER] {}.{} | method={} uri={} params={}", controller, action, method, uri,
                    params);
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("[END]  [CONTROLLER] {}.{} | method={} uri={} duration={}ms", controller, action, method, uri,
                    end - start);
        }
    }

    @Around("reservationService()")
    public Object logReservationService(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        final String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        try {
            log.info("[START][SERVICE] {}.{}", className, methodName);
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("[END]  [SERVICE] {}.{} | duration={}ms", className, methodName, end - start);
        }
    }

    private Map<String, String> getParams(final HttpServletRequest request) {
        final Map<String, String> paramMap = new HashMap<>();
        final Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            final String param = params.nextElement();
            final String value = request.getParameter(param);
            paramMap.put(param.replaceAll("\\.", "-"), value);
        }

        return paramMap;
    }
}
