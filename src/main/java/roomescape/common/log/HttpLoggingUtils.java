package roomescape.common.log;

import java.nio.charset.StandardCharsets;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class HttpLoggingUtils {

    private HttpLoggingUtils() {
    }

    public static String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Failed to read request body";
        }
    }

    public static String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Failed to read response body";
        }
    }
}
