package roomescape.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        log.info(request.getMethod() + " " + request.getRequestURI() + "요청");
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {
        if (ex != null) {
            log.error(request.getMethod() + " " + request.getRequestURI() + "처리 중 예외 발생: {}", ex.getMessage());
            return;
        }
        log.info(request.getMethod() + " " + request.getRequestURI() + "처리 성공");
    }
}
