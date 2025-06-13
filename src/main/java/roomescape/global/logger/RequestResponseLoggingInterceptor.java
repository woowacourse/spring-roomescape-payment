package roomescape.global.logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class RequestResponseLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        String requestURI = request.getRequestURI();
        log.info("[API REQUEST] URL = {}, currentTime = {}", requestURI, LocalDateTime.now());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
        String requestURI = request.getRequestURI();
        int status = response.getStatus();
        long requestDuration = calculateDuration(request);

        log.info("[API RESPONSE] URL = {} | Status = {} | Duration = {}ms", requestURI, status, requestDuration);
    }

    private long calculateDuration(HttpServletRequest request) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }
}
