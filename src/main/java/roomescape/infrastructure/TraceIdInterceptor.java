package roomescape.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID = "traceId";
    private static final String MEMBER_ID_ATTRIBUTE = "memberId";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
    {
        final String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);
        response.setHeader(TRACE_ID, traceId);

        String memberId = request.getHeader(MEMBER_ID_ATTRIBUTE);
        if (memberId == null) {
            memberId = "anonymous";
        }
        MDC.put(MEMBER_ID_ATTRIBUTE, memberId);

        MDC.put("api", request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex) {
        MDC.remove(TRACE_ID);
    }
}
