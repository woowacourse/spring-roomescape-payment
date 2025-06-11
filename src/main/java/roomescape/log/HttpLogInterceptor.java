package roomescape.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class HttpLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        request.setAttribute("startTime", System.currentTimeMillis());
        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        log.info("Request: method={} uri={}", httpMethod, requestURI);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        log.info("Response: method={}, uri={}, status={}, duration={}ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
    }
}
