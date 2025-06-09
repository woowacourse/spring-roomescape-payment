package roomescape.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        LOGGER.info("[API REQUEST] {}", requestURI);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        int status = response.getStatus();

        LOGGER.info("[API RESPONSE] {}: {}", requestURI, status);
        if (status >= 200 && status < 300) {
            LOGGER.info("[API RESPONSE] {}: {}", requestURI, status);
        } else if (status >= 400 && status < 500) {
            LOGGER.warn("[API RESPONSE - CLIENT ERROR] {}: {}", requestURI, status);
        } else if (status >= 500) {
            LOGGER.error("[API RESPONSE - SERVER ERROR] {}: {}", requestURI, status);
        }
    }
}
