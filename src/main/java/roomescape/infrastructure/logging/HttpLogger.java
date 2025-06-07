package roomescape.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class HttpLogger extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public HttpLogger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();

        ContentCachingRequestWrapper wrappedRequest = wrapRequest(request, requestId);
        ContentCachingResponseWrapper wrappedResponse = wrapResponse(response);

        logRequestLine(wrappedRequest, requestId);
        logRequestHeaders(wrappedRequest, requestId);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        logSummary(wrappedRequest, wrappedResponse, requestId);
        logResponseHeaders(wrappedResponse, requestId);
        logResponseBody(wrappedResponse, requestId);

        wrappedResponse.copyBodyToResponse();
    }

    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request, String requestId) {
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
        wrapper.setAttribute("requestId", requestId);
        return wrapper;
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        return new ContentCachingResponseWrapper(response);
    }

    private void logRequestLine(ContentCachingRequestWrapper request, String requestId) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        log.info("RequestId={} <<Request>> URI={}{}",
                requestId,
                uri,
                query != null ? "?" + query : ""
        );
    }

    private void logRequestHeaders(ContentCachingRequestWrapper request, String requestId) {
        String headers = Collections.list(request.getHeaderNames()).stream()
                .map(name -> name + "=" + request.getHeader(name))
                .collect(Collectors.joining(", "));
        log.info("RequestId={} <<Request>> Headers: {}", requestId, headers);
    }

    private void logSummary(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            String requestId
    ) {
        log.info("RequestId={} <<Summary>> {} {} → {}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus()
        );
    }

    private void logResponseHeaders(ContentCachingResponseWrapper response, String requestId) {
        String headers = response.getHeaderNames().stream()
                .map(name -> name + "=" + response.getHeader(name))
                .collect(Collectors.joining(", "));
        log.info("RequestId={} <<Response>> Headers: {}", requestId, headers);
    }

    private void logResponseBody(ContentCachingResponseWrapper response, String requestId) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            log.info("RequestId={} <<Response>> Body=", requestId);
            return;
        }
        String bodyLog;
        try {
            bodyLog = objectMapper.readTree(content).toPrettyString();
        } catch (Exception e) {
            bodyLog = response.getContentType() + " body parsing failed";
        }
        log.info("RequestId={} <<Response>> Body=\n{}", requestId, bodyLog);
    }
}
