package roomescape.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import roomescape.common.exception.LoggableException;

@Component
@Slf4j
public class LoggingHandlerInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("handle request successfully - method: {}, uri: {}, ip: {}, userAgent: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex instanceof LoggableException) {
            String message = String.format("exception invoked while handling request - method: %s, uri: %s, ip: %s, userAgent: %s, message: %s",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    ex.getMessage()
            );
            switch (((LoggableException) ex).getLogLevel()) {
                case ERROR, FATAL -> log.error(message);
                case WARN -> log.warn(message);
                case INFO -> log.info(message);
                case DEBUG -> log.debug(message);
                case TRACE -> log.trace(message);
            }
        }
    }
}
