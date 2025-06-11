package roomescape.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());

        MDC.put("requestId", requestId);

        log.info("[API_REQUEST] {} {} 요청", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            if (ex != null) {
                log.error("[API_RESPONSE_ERROR] {} {} 처리 중 예외 발생: {}, HTTP status: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), response.getStatus());
            } else {
                log.info("[API_RESPONSE] {} {} 처리 성공, 응답까지 소요 시간: {} ms, HTTP status: {}", request.getMethod(), request.getRequestURI(), duration, response.getStatus());
            }
        } else {
            log.warn("[API_RESPONSE_ERROR] {} {} 요청 시작 시간을 찾을 수 없음, HTTP status: {}", request.getMethod(), request.getRequestURI(), response.getStatus());
        }

        MDC.remove("requestId");
    }
}
