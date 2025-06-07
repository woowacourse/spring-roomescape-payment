package roomescape.global.log;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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
public class LogAspect {

    @Pointcut("execution(* roomescape..*(..)) && !execution(* roomescape.global..*(..))")
    public void all() {
    }

    @Pointcut("execution(* roomescape..*Controller.*(..))")
    public void controller() {
    }

    @Around("all()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("[EXECUTED] {} in {} ms", joinPoint.getSignature(), end - start);
        }
    }

    @Around("controller()")
    public Object logRequestInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();

        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            String fullUrl = (query != null) ? uri + "?" + query : uri;

            Map<String, Object> logMap = new LinkedHashMap<>();
            logMap.put("client_ip", getClientIP(request));
            logMap.put("http_method", method);
            logMap.put("request_url", fullUrl);
            logMap.put("handler_class", joinPoint.getSignature().getDeclaringTypeName());
            logMap.put("handler_method", joinPoint.getSignature().getName());

            log.info("[REQUEST] {{}}", formatLogMap(logMap));
        } catch (Exception e) {
            log.error("[ERROR] ", e);
        }

        return joinPoint.proceed();
    }

    private String getClientIP(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    private String formatLogMap(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("\n");
        map.forEach((key, value) -> sb.append("  ").append(key).append(" = ").append(value).append("\n"));
        return sb.toString();
    }
}
