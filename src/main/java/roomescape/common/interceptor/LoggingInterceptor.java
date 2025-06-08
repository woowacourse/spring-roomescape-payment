package roomescape.common.interceptor;

import static java.util.UUID.randomUUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        request.setAttribute("uuid", randomUUID().toString());
        log.atInfo().log("request : uuid={}, path={}", request.getAttribute("uuid"), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        log.atInfo().log("response : uuid={}, path={}", request.getAttribute("uuid"), request.getRequestURI());
    }
}
