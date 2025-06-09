package roomescape.log;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import roomescape.auth.domain.AuthRole;
import roomescape.auth.domain.AuthTokenExtractor;
import roomescape.auth.domain.AuthTokenProvider;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    private final AuthTokenExtractor<String> authTokenExtractor;
    private final AuthTokenProvider authTokenProvider;

    @Pointcut("execution(* roomescape..*(..))")
    public void all() {
    }

    @Pointcut("execution(* roomescape..ui..*(..))")
    public void controller() {
    }

    @Around("all()")
    public Object logExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            // 메서드 실행 시간 테스트
            log.info("[EXECUTED] {} in {} ms", joinPoint.getSignature(), end - start);
        }
    }

    @Around("controller()")
    public Object logRequestInfo(final ProceedingJoinPoint joinPoint) throws Throwable {
        final ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.debug("[REQUEST] No request context available");
            return joinPoint.proceed();
        }
        final HttpServletRequest request = attributes.getRequest();

        try {
            final String method = request.getMethod();
            final String uri = request.getRequestURI();
            final String query = request.getQueryString();
            final String fullUrl = (query != null) ? uri + "?" + query : uri;

            final Map<String, Object> logMap = new LinkedHashMap<>();

            logMap.put("client_ip", getClientIP(request));
            logMap.put("http_method", method);
            logMap.put("request_url", fullUrl);
            logMap.put("handler_class", joinPoint.getSignature().getDeclaringTypeName());
            logMap.put("handler_method", joinPoint.getSignature().getName());
            logMap.put("role", getRole(request));
            logMap.put("cookies", getCookies(request));

            log.info("[REQUEST] {}", formatLogMap(logMap));
        } catch (Exception e) {
            log.error("[ERROR] ", e);
        }

        return joinPoint.proceed();
    }

    private String getClientIP(final HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }

    private String getRole(final HttpServletRequest request) {
        final String accessToken = authTokenExtractor.extract(request);
        if (!authTokenProvider.isValidToken(accessToken)) {
            return AuthRole.GUEST.getRoleName();
        }

        final AuthRole role = authTokenProvider.getRole(accessToken);
        return role.getRoleName();
    }

    private String getCookies(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "{}";
        }

        final Map<String, Object> cookieMap = new LinkedHashMap<>();
        for (Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie.getValue());
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
