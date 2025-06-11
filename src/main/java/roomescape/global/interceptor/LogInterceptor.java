package roomescape.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("[API REQUEST] {}", requestURI);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        int status = response.getStatus();

        if (status >= 400 && status < 500) {
            log.warn("[API RESPONSE - CLIENT ERROR] {}: {}", requestURI, status);
        } else if (status >= 500) {
            log.error("[API RESPONSE - SERVER ERROR] {}: {}", requestURI, status);
        }
    }
}
