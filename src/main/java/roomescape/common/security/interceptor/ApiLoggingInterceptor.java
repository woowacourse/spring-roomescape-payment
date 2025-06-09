package roomescape.common.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class ApiLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        MDC.clear();
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        MDC.put("traceId", traceId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        MDC.put("clientIp", request.getRemoteAddr());
        MDC.put("userAgent", shortenUserAgent(request.getHeader("User-Agent")));

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            MDC.put("controller", hm.getBeanType().getSimpleName());
            MDC.put("action", hm.getMethod().getName());
        }
        log.info("요청 시작");

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

        try {
            Long startTime = (Long) request.getAttribute("startTime");
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                MDC.put("duration", duration + "ms");
                MDC.put("status", String.valueOf(response.getStatus()));

                if (ex != null) {
                    MDC.put("exception", ex.getClass().getSimpleName());
                    log.error("요청 실패", ex);
                } else if (response.getStatus() >= 400) {
                    log.warn("요청 에러");
                } else {
                    log.info("요청 완료");
                }
            }
        } finally {
            MDC.clear();
        }
    }

    private String shortenUserAgent(String userAgent) {
        if (userAgent == null) {
            return "UNKNOWN";
        }
        return userAgent.length() > 50 ? userAgent.substring(0, 50) + "..." : userAgent;
    }
}
