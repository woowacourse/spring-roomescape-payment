package roomescape.common.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@Profile("!prod")
public class DetailRequestResponseLogger extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String requestBody = getBodyAsUtf8String(requestWrapper.getContentAsByteArray());
            String responseBody = getBodyAsUtf8String(responseWrapper.getContentAsByteArray());
            String cookieHeader = getCookieHeader(requestWrapper);

            logForRequest(request, cookieHeader, requestBody);
            logForResponse(response, responseWrapper, duration, responseBody);

            responseWrapper.copyBodyToResponse();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/favicon");
    }

    private String getBodyAsUtf8String(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "(empty)";
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String getCookieHeader(ContentCachingRequestWrapper requestWrapper) {
        String value = requestWrapper.getHeader("cookie");
        if (value == null) {
            return "(empty)";
        }
        return value;
    }

    private void logForRequest(HttpServletRequest request, String cookieHeader, String requestBody) {
        log.info("[Request] {} {}, 쿠키 값: {}\n요청 바디: {}",
                request.getMethod(), request.getRequestURI(), cookieHeader, requestBody);
    }

    private void logForResponse(HttpServletResponse response, ContentCachingResponseWrapper responseWrapper,
                                long duration, String responseBody) {
        String contentType = responseWrapper.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            log.info("[Response] Status: {}, Duration: {}ms\n응답 바디: {}",
                    response.getStatus(), duration, responseBody);
            return;
        }
        log.info("[Response] Status: {}, Duration: {}ms\n응답 바디 생략 (Content-Type: {})", response.getStatus(), duration,
                contentType);
    }
}
