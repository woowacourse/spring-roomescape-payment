package roomescape.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());
        log.info(request.getMethod() + " " + request.getRequestURI() + " 요청");
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {

        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            if (ex != null) {
                log.error(request.getMethod() + " " + request.getRequestURI() + " 처리 중 예외 발생: {}", ex.getMessage());
                return;
            }
            log.info(request.getMethod() + " " + request.getRequestURI() + " 처리 성공, 응답까지 소요 시간: {} ms", duration);
            return;
        }
        log.warn(request.getMethod() + " " + request.getRequestURI() + " 처리 성공");
    }
}
