package roomescape.common.log;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    @Pointcut("execution(* roomescape..*(..))")
    public void all() {
    }

    @Pointcut("execution(* roomescape..ui..*(..))")
    public void controller() {
    }

    @Around("all()")
    public Object logExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            final long end = System.currentTimeMillis();
            final long executionTime = end - start;

            if (executionTime > 1000) {
                log.warn("[SLOW EXECUTION] {} took {} ms", joinPoint.getSignature(), executionTime);
            } else {
                log.debug("[EXECUTED] {} in {} ms", joinPoint.getSignature(), executionTime);
            }
        }
    }

    @Around("controller()")
    public Object logRequestInfo(final ProceedingJoinPoint joinPoint) throws Throwable {
        final HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();

        try {
            final String method = request.getMethod();
            final String uri = request.getRequestURI();
            final String query = request.getQueryString();
            final String fullUrl = (query != null) ? uri + "?" + query : uri;

            final Map<String, Object> logMap = new LinkedHashMap<>();
            logMap.put("http_method", method);
            logMap.put("request_url", fullUrl);
            logMap.put("handler_class", joinPoint.getSignature().getDeclaringTypeName());
            logMap.put("handler_method", joinPoint.getSignature().getName());
            logMap.put("cookies", getCookieInfo(request));

            log.info("[REQUEST] {}", formatLogMap(logMap));
        } catch (Exception e) {
            log.error("[LOG ERROR] Failed to log request info: {}", e.getMessage(), e);
        }

        return joinPoint.proceed();
    }

    private String getCookieInfo(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return "no cookies";
        }

        final Map<String, Object> cookieMap = new LinkedHashMap<>();
        for (Cookie cookie : cookies) {
            if ("ACCESS_TOKEN".equals(cookie.getName())) {
                cookieMap.put(cookie.getName(), cookie.getValue());
            }
        }

        return "{ " + formatLogMap(cookieMap) + " }";
    }

    private String formatLogMap(final Map<String, Object> map) {
        final StringBuilder sb = new StringBuilder("\n");
        map.forEach((key, value) ->
                sb.append("  ").append(key).append(" = ").append(value).append("\n")
        );
        return sb.toString();
    }
}
