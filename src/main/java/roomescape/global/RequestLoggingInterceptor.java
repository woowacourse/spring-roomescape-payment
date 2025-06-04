package roomescape.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler) {
        StringBuilder requestInfo = new StringBuilder();
        requestInfo.append("\n--- Interceptor HTTP Request ---");
        appendRequestMethodAndUri(request, requestInfo);
        appendRequestQueryString(request, requestInfo);
        appendRequestHeader(request, requestInfo);
        appendRequestBody(request, requestInfo);
        log.info(requestInfo.toString());
        return true;
    }

    private void appendRequestMethodAndUri(HttpServletRequest request, StringBuilder requestInfo) {
        requestInfo.append("\nMethod: ").append(request.getMethod());
        requestInfo.append("\nURI: ").append(request.getRequestURI());
    }

    private void appendRequestQueryString(HttpServletRequest request, StringBuilder requestInfo) {
        if (request.getQueryString() != null) {
            requestInfo.append("?").append(request.getQueryString());
        }
    }

    private void appendRequestHeader(HttpServletRequest request, StringBuilder requestInfo) {
        requestInfo.append("\nHeaders:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestInfo.append("\n  ").append(headerName).append(": ").append(request.getHeader(headerName));
        }
    }

    private void appendRequestBody(HttpServletRequest request, StringBuilder requestInfo) {
        if (request instanceof ContentCachingRequestWrapper cachingRequest) {
            byte[] content = cachingRequest.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                requestInfo.append("\nBody: ").append(tryFormatJson(body));
            } else {
                requestInfo.append("\nBody: Empty or already copied to original request stream");
            }
        } else {
            requestInfo.append("\nBody: Request Body가 ContentCachingRequestWrapper가 아니기 때문에, 내용을 읽을 수 없습니다.");
        }
    }

    @Override
    public void afterCompletion(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler, @Nullable final Exception ex) {
        StringBuilder responseInfo = new StringBuilder();
        responseInfo.append("\n--- Interceptor HTTP Response ---");
        appendUriAndStatus(request, response, responseInfo);
        appendResponseHeaders(response, responseInfo);
        appendResponseBody(response, responseInfo);
        log.info(responseInfo.toString());
    }

    private void appendUriAndStatus(HttpServletRequest request, HttpServletResponse response, StringBuilder responseInfo) {
        responseInfo.append("\nFor Request URI: ").append(request.getRequestURI());
        responseInfo.append("\nStatus: ").append(response.getStatus());
    }

    private void appendResponseHeaders(HttpServletResponse response, StringBuilder responseInfo) {
        responseInfo.append("\nHeaders:");
        response.getHeaderNames().forEach(headerName -> responseInfo.append("\n  ").append(headerName).append(": ")
                .append(response.getHeader(headerName)));
    }

    private void appendResponseBody(HttpServletResponse response, StringBuilder responseInfo) {
        if (response instanceof ContentCachingResponseWrapper cachingResponse) {
            byte[] content = cachingResponse.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                responseInfo.append("\nBody: ").append(tryFormatJson(body));
            } else {
                responseInfo.append("\nBody: Empty or already copied to original response stream");
            }
        } else {
            responseInfo.append("\nBody: Response Body가 ContentCachingResponseWrapper가 아니기 때문에, 내용을 읽을 수 없습니다.");
        }
    }

    private String tryFormatJson(String body) {
        String trimmedBody = body.trim();
        boolean isJson = !body.isBlank() && (trimmedBody.startsWith("{") && trimmedBody.endsWith("}") || trimmedBody.startsWith("[") && trimmedBody.endsWith("]"));
        if (isJson) {
            try {
                Object json = objectMapper.readValue(trimmedBody, Object.class);
                return "\n" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } catch (JsonProcessingException e) {
                return body;
            }
        }
        return "Not JSON";
    }
}
