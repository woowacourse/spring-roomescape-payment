package roomescape.common.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiLoggingInterceptor.class);

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        log.info("[REQUEST] {} {}", request.getMethod(), getFullRequestUri(request));
        return true;
    }

    @Override
    public void afterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception ex
    ) {
        log.info("[RESPONSE] HTTP Status: {}", response.getStatus());
    }

    private String getFullRequestUri(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        final String query = request.getQueryString();

        if (query != null) {
            return uri + "?" + query;
        }
        return uri;
    }
}
