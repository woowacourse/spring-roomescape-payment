package roomescape.global;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import roomescape.log.AccessLogger;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_KEY = "requestId";
    private final AccessLogger accessLogger;

    public LoggingFilter(AccessLogger accessLogger) {
        this.accessLogger = accessLogger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        MDC.put(REQUEST_ID_KEY, UUID.randomUUID().toString());

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            accessLogger.log(wrappedRequest, wrappedResponse, filterChain);
        } finally {
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }
}