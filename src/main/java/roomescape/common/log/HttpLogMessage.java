package roomescape.common.log;

import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public record HttpLogMessage(
    String httpMethod,
    String requestUri,
    int httpStatus,
    String clientIp,
    String requestBody,
    String responseBody
) {

    public static HttpLogMessage createInstance(ContentCachingRequestWrapper requestWrapper,
        ContentCachingResponseWrapper responseWrapper) {
        return new HttpLogMessage(
            requestWrapper.getMethod(),
            requestWrapper.getRequestURI(),
            responseWrapper.getStatus(),
            requestWrapper.getRemoteAddr(),
            HttpLoggingUtils.getRequestBody(requestWrapper),
            HttpLoggingUtils.getResponseBody(responseWrapper)
        );
    }
}
