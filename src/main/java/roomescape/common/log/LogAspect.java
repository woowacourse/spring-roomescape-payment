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

    @Pointcut("execution(* roomescape..application.service.*CommandService.*(..))")
    public void dataModificationOperations() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void dataModifyingRequests() {
    }

    @Around("dataModificationOperations()")
    public Object monitorCommandPerformance(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        final String operation = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();

            final long executionTime = System.currentTimeMillis() - start;

            if (executionTime > 5000) {
                log.warn("[SLOW_COMMAND] {} took {}ms - 데이터 변경 작업이 지연되고 있습니다",
                        operation, executionTime);
            } else if (executionTime > 1000) {
                log.info("[COMMAND_PERFORMANCE] {} took {}ms", operation, executionTime);
            } else {
                log.debug("[COMMAND_OK] {} completed in {}ms", operation, executionTime);
            }

            return result;

        } catch (Exception e) {
            final long executionTime = System.currentTimeMillis() - start;
            log.error("[COMMAND_FAILED] {} failed after {}ms: {}",
                    operation, executionTime, e.getMessage());
            throw e;
        }
    }

    @Around("dataModifyingRequests()")
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

            log.debug("[REQUEST] {}", formatLogMap(logMap));
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
                cookieMap.put(cookie.getName(), maskSensitiveData(cookie.getValue()));
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

    private String maskSensitiveData(String value) {
        return value.length() > 8 ?
                value.substring(4, 8) + "****" : "****";
    }
}
