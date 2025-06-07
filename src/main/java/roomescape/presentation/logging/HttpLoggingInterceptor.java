package roomescape.presentation.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class HttpLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "requestId";
    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(REQUEST_ID, requestId);

        log.info("HTTP REQUEST: method={}, uri={}", request.getMethod(), request.getRequestURI());

        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) throws Exception {
        long duration = System.currentTimeMillis() - (Long) request.getAttribute(START_TIME);

        log.info("HTTP RESPONSE: status={}, duration={}ms", response.getStatus(), duration);

        MDC.remove(REQUEST_ID);
    }
}
