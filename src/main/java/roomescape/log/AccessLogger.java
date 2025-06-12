package roomescape.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@RequiredArgsConstructor
public class AccessLogger {

    private static final Logger log = LoggerFactory.getLogger(AccessLogger.class);
    private static final int HTTP_CLIENT_ERROR_THRESHOLD = 400;
    private static final long SLOW_REQUEST_THRESHOLD_MS = 3000;

    private final LoggingPolicy loggingPolicy;
    private final LogMessageFormatter formatter;

    public void log(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        boolean shouldLog = loggingPolicy.shouldLog(request);

        if (shouldLog) {
            log.info(formatter.formatRequest(request));
        }

        try {
            filterChain.doFilter(request, response);
            long elapsedTime = System.currentTimeMillis() - startTime;
            logResponse(response, elapsedTime, shouldLog);
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.error(formatter.formatExceptionResponse(response, elapsedTime), e);
            throw e;
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, long elapsedTime, boolean loggedRequest) {
        int status = response.getStatus();
        boolean isError = status >= HTTP_CLIENT_ERROR_THRESHOLD;
        boolean isSlow = elapsedTime > SLOW_REQUEST_THRESHOLD_MS;

        if (isError) {
            log.warn(formatter.formatErrorResponse(response, elapsedTime));
            return;
        }

        if (isSlow) {
            log.warn(formatter.formatSlowResponse(response, elapsedTime));
            return;
        }

        if (loggedRequest) {
            log.info(formatter.formatResponse(response, elapsedTime));
        }
    }
}
