package roomescape.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LogMessageFormatter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String HTML_CONTENT_TYPE = "text/html";

    public String formatRequest(ContentCachingRequestWrapper request) {
        String requestBody = extractContent(request.getContentAsByteArray());
        String formattedBody = formatJsonIfPossible(requestBody);

        return String.format("REQ [%s] %s %s from %s params=%s\n%s",
                MDC.get(REQUEST_ID_KEY),
                request.getMethod(),
                request.getRequestURI(),
                getClientIpAddress(request),
                request.getQueryString(),
                formattedBody);
    }

    public String formatResponse(ContentCachingResponseWrapper response, long elapsedTime) {
        String responseBody = shouldLogResponseBody(response)
                ? formatJsonIfPossible(extractContent(response.getContentAsByteArray()))
                : "[body omitted]";

        return String.format("RES [%s] %d %dms\n%s",
                MDC.get(REQUEST_ID_KEY),
                response.getStatus(),
                elapsedTime,
                responseBody);
    }

    public String formatSlowResponse(ContentCachingResponseWrapper response, long elapsedTime) {
        String responseBody = formatJsonIfPossible(extractContent(response.getContentAsByteArray()));
        return String.format("SLOW [%s] %d %dms\n%s",
                MDC.get(REQUEST_ID_KEY),
                response.getStatus(),
                elapsedTime,
                responseBody);
    }

    public String formatErrorResponse(ContentCachingResponseWrapper response, long elapsedTime) {
        String responseBody = formatJsonIfPossible(extractContent(response.getContentAsByteArray()));
        return String.format("ERR [%s] %d %dms\n%s",
                MDC.get(REQUEST_ID_KEY),
                response.getStatus(),
                elapsedTime,
                responseBody);
    }

    public String formatExceptionResponse(ContentCachingResponseWrapper response, long elapsedTime) {
        String responseBody = formatJsonIfPossible(extractContent(response.getContentAsByteArray()));
        return String.format("EXC [%s] %d %dms\n%s",
                MDC.get(REQUEST_ID_KEY),
                response.getStatus(),
                elapsedTime,
                responseBody);
    }

    private boolean shouldLogResponseBody(ContentCachingResponseWrapper response) {
        String contentType = response.getContentType();
        return contentType == null || !contentType.contains(HTML_CONTENT_TYPE);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractContent(byte[] content) {
        if (content == null || content.length == 0) {
            return "";
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    private String formatJsonIfPossible(String body) {
        if (body.isEmpty() || !isJsonLike(body)) {
            return body;
        }
        try {
            Object json = OBJECT_MAPPER.readValue(body, Object.class);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return body;
        }
    }

    private boolean isJsonLike(String body) {
        String trimmed = body.trim();
        return trimmed.startsWith("{") || trimmed.startsWith("[");
    }
}
