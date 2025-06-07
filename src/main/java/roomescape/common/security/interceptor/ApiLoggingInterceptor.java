package roomescape.common.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        String controllerInfo = "";
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            controllerInfo = String.format("%s.%s",
                    hm.getBeanType().getSimpleName(),
                    hm.getMethod().getName());
        }

        log.info("API 요청 - {} {} | {} | IP: {} | UA: {}",
                method, uri, controllerInfo, clientIp,
                userAgent != null ? userAgent.substring(0, Math.min(50, userAgent.length())) : "");

        request.setAttribute("startTime", System.currentTimeMillis());
        request.setAttribute("requestInfo", String.format("%s %s", method, uri));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

        Long startTime = (Long) request.getAttribute("startTime");
        String requestInfo = (String) request.getAttribute("requestInfo");

        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = response.getStatus();

            if (ex != null) {
                log.error("API 실패 - {} | {}ms | 상태: {} | 예외: {}",
                        requestInfo, duration, statusCode, ex.getClass().getSimpleName());
            } else if (statusCode >= 400) {
                log.warn("API 에러 - {} | {}ms | 상태: {}",
                        requestInfo, duration, statusCode);
            } else {
                log.info("API 완료 - {} | {}ms | 상태: {}",
                        requestInfo, duration, statusCode);
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
